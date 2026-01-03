package org.dromara.system.service;

import org.dromara.system.domain.MusicComment;
import org.dromara.system.domain.vo.MusicCommentVo;
import org.dromara.system.domain.bo.MusicCommentBo;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.mybatisflex.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 音乐评论Service接口
 *
 * @author momao
 * @date 2025-12-30
 */
public interface IMusicCommentService {

    /**
     * 查询音乐评论
     *
     * @param id 主键
     * @return 音乐评论
     */
    MusicCommentVo queryById(Long id);

    /**
     * 分页查询音乐评论列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 音乐评论分页列表
     */
    TableDataInfo<MusicCommentVo> queryPageList(MusicCommentBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的音乐评论列表
     *
     * @param bo 查询条件
     * @return 音乐评论列表
     */
    List<MusicCommentVo> queryList(MusicCommentBo bo);

    /**
     * 新增音乐评论
     *
     * @param bo 音乐评论
     * @return 是否新增成功
     */
    Boolean insertByBo(MusicCommentBo bo);

    /**
     * 修改音乐评论
     *
     * @param bo 音乐评论
     * @return 是否修改成功
     */
    Boolean updateByBo(MusicCommentBo bo);

    /**
     * 校验并批量删除音乐评论信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
