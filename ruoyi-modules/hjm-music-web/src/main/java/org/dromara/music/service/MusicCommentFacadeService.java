package org.dromara.music.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.mybatisflex.helper.DataBaseHelper;
import org.dromara.music.domain.Music;
import org.dromara.music.domain.MusicComment;
import org.dromara.music.domain.bo.PortalCommentSubmitBo;
import org.dromara.music.domain.vo.MusicCommentVo;
import org.dromara.music.domain.vo.OpenCommentVo;
import org.dromara.music.mapper.MusicCommentMapper;
import org.dromara.music.mapper.MusicMapper;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.dromara.music.domain.table.MusicCommentTableDef.MUSIC_COMMENT;
import static org.dromara.music.domain.table.MusicTableDef.MUSIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicCommentFacadeService {

    private static final String AUDIT_APPROVED = "1";
    private static final String PUBLIC_VISIBLE = "1";

    private final MusicMapper musicMapper;
    private final MusicCommentMapper musicCommentMapper;
    private final MusicInteractionService musicInteractionService;

    @DubboReference
    private final RemoteUserService remoteUserService;

    public List<OpenCommentVo> queryPublicComments(Long musicId) {
        ensurePublicMusic(musicId);
        List<MusicCommentVo> comments = musicCommentMapper.selectVoList(QueryWrapper.create()
            .where(MUSIC_COMMENT.MUSIC_ID.eq(musicId))
            .orderBy(MUSIC_COMMENT.CREATE_TIME.asc())
            .orderBy(MUSIC_COMMENT.ID.asc()));
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> nicknameMap = loadNicknameMap(comments);
        Map<Long, OpenCommentVo> commentMap = new LinkedHashMap<>();
        List<OpenCommentVo> roots = new ArrayList<>();
        for (MusicCommentVo comment : comments) {
            OpenCommentVo vo = toOpenComment(comment, nicknameMap.get(comment.getUserId()));
            commentMap.put(vo.getId(), vo);
        }
        for (OpenCommentVo comment : commentMap.values()) {
            if (comment.getParentId() == null || comment.getParentId() == 0L) {
                roots.add(comment);
                continue;
            }
            OpenCommentVo parent = commentMap.get(comment.getParentId());
            if (parent == null) {
                roots.add(comment);
                continue;
            }
            if (parent.getReplies() == null) {
                parent.setReplies(new ArrayList<>());
            }
            parent.getReplies().add(comment);
        }
        roots.forEach(this::sortRepliesRecursively);
        roots.sort(Comparator.comparing(OpenCommentVo::getCreateTime).thenComparing(OpenCommentVo::getId));
        musicInteractionService.fillCommentLikeStats(roots);
        return roots;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long submitComment(Long musicId, Long userId, PortalCommentSubmitBo bo) {
        ensurePublicMusic(musicId);
        Long rootId = normalizeRootId(bo.getRootId());
        Long parentId = normalizeParentId(bo.getParentId());
        validateReplyTarget(musicId, rootId, parentId);

        MusicComment entity = new MusicComment();
        entity.setId(DataBaseHelper.nextId());
        entity.setMusicId(musicId);
        entity.setUserId(userId);
        entity.setContent(bo.getContent().trim());
        entity.setRootId(rootId);
        entity.setParentId(parentId);
        entity.setCreateBy(userId);
        entity.setCreateTime(new Date());
        entity.setUpdateBy(userId);
        entity.setUpdateTime(entity.getCreateTime());
        entity.setDelFlag("0");
        musicCommentMapper.insert(entity);
        musicInteractionService.changeCommentCount(musicId, 1L);
        return entity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteComment(Long musicId, Long commentId, Long userId) {
        MusicComment comment = musicCommentMapper.selectOneById(commentId);
        if (comment == null || !Objects.equals(comment.getMusicId(), musicId)) {
            return false;
        }
        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new ServiceException("无权删除该评论");
        }

        List<MusicCommentVo> comments = musicCommentMapper.selectVoList(QueryWrapper.create()
            .where(MUSIC_COMMENT.ID.eq(commentId).or(MUSIC_COMMENT.ROOT_ID.eq(commentId))));
        Set<Long> deleteIds = comments.stream().map(MusicCommentVo::getId).collect(Collectors.toSet());
        if (deleteIds.isEmpty()) {
            deleteIds = Set.of(commentId);
        }
        int rows = musicCommentMapper.deleteByQuery(QueryWrapper.create().where(MUSIC_COMMENT.ID.in(deleteIds)));
        if (rows > 0) {
            musicInteractionService.changeCommentCount(musicId, -rows);
            return true;
        }
        return false;
    }

    private void validateReplyTarget(Long musicId, Long rootId, Long parentId) {
        if (parentId == null || parentId == 0L) {
            return;
        }
        MusicComment parent = musicCommentMapper.selectOneById(parentId);
        if (parent == null || !Objects.equals(parent.getMusicId(), musicId)) {
            throw new ServiceException("回复目标不存在");
        }
        if (rootId != null && rootId > 0L) {
            MusicComment root = musicCommentMapper.selectOneById(rootId);
            if (root == null || !Objects.equals(root.getMusicId(), musicId)) {
                throw new ServiceException("顶级评论不存在");
            }
        }
    }

    private Long normalizeRootId(Long rootId) {
        return rootId == null ? 0L : rootId;
    }

    private Long normalizeParentId(Long parentId) {
        return parentId == null ? 0L : parentId;
    }

    private void ensurePublicMusic(Long musicId) {
        Music music = musicMapper.selectOneById(musicId);
        if (music == null || !AUDIT_APPROVED.equals(music.getAuditStatus()) || !PUBLIC_VISIBLE.equals(music.getIsPublic())) {
            throw new ServiceException("歌曲不存在或暂不可访问");
        }
    }

    private Map<Long, String> loadNicknameMap(List<MusicCommentVo> comments) {
        List<Long> userIds = comments.stream().map(MusicCommentVo::getUserId).filter(Objects::nonNull).distinct().toList();
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            List<RemoteUserVo> users = remoteUserService.selectListByIds(userIds);
            if (users == null || users.isEmpty()) {
                return Collections.emptyMap();
            }
            return users.stream().collect(Collectors.toMap(RemoteUserVo::getUserId, item -> {
                if (item.getNickName() != null && !item.getNickName().isBlank()) {
                    return item.getNickName();
                }
                return item.getUserName();
            }, (left, right) -> left));
        } catch (Exception ex) {
            log.warn("批量查询评论用户昵称失败", ex);
            return Collections.emptyMap();
        }
    }

    private OpenCommentVo toOpenComment(MusicCommentVo comment, String nickname) {
        OpenCommentVo vo = new OpenCommentVo();
        vo.setId(comment.getId());
        vo.setMusicId(comment.getMusicId());
        vo.setUserId(comment.getUserId());
        vo.setNickName(nickname);
        vo.setContent(comment.getContent());
        vo.setRootId(comment.getRootId());
        vo.setParentId(comment.getParentId());
        vo.setCreateTime(comment.getCreateTime());
        vo.setLikeCount(0L);
        vo.setReplies(new ArrayList<>());
        return vo;
    }

    private void sortRepliesRecursively(OpenCommentVo comment) {
        if (comment.getReplies() == null || comment.getReplies().isEmpty()) {
            return;
        }
        comment.getReplies().sort(Comparator.comparing(OpenCommentVo::getCreateTime).thenComparing(OpenCommentVo::getId));
        comment.getReplies().forEach(this::sortRepliesRecursively);
    }
}
