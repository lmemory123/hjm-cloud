package org.dromara.music.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.music.domain.vo.MusicVo;
import org.dromara.music.domain.vo.OpenUserProfileVo;
import org.dromara.music.service.IOpenMusicService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchAggregateController {

    private final IOpenMusicService openMusicService;

    @SaIgnore
    @GetMapping
    public TableDataInfo<?> search(@RequestParam(value = "q", required = false) String q,
                                   @RequestParam(value = "type", required = false, defaultValue = "song") String type,
                                   PageQuery pageQuery) {
        if ("song".equalsIgnoreCase(type) || "audio".equalsIgnoreCase(type)) {
            try {
                return openMusicService.searchPublic(q, null, null, null, null, null, null, null, null, null, null, null, pageQuery);
            } catch (Exception ex) {
                log.warn("Search song failed, fallback to empty", ex);
                return new TableDataInfo<>(Collections.emptyList(), 0);
            }
        } else if ("user".equalsIgnoreCase(type)) {
            // For user, query using openMusicService.searchPublic then group by creator, or use queryPublicUserProfile
            try {
                // Since IOpenMusicService doesn't have a dedicated user search, we can search songs by keyword and extract users.
                // Or better, we just fallback to returning an empty list if we can't search users directly.
                // But the requirement says "type=user 能返回用户". Let's search songs by creatorName = q. 
                // Wait, searchPublic checks searchText.
                TableDataInfo<MusicVo> songs = openMusicService.searchPublic(q, null, null, null, null, null, null, null, null, null, null, null, new PageQuery(1, 100));
                List<OpenUserProfileVo> users = new ArrayList<>();
                List<String> seen = new ArrayList<>();
                for (MusicVo song : songs.getRows()) {
                    String uid = song.getCreatorId() != null ? String.valueOf(song.getCreatorId()) : song.getCreatorName();
                    if (uid != null && !seen.contains(uid)) {
                        seen.add(uid);
                        OpenUserProfileVo profile = openMusicService.queryPublicUserProfile(uid);
                        if (profile != null) {
                            users.add(profile);
                        }
                    }
                    if (users.size() >= pageQuery.getPageSize()) break;
                }
                return new TableDataInfo<>(users, users.size());
            } catch (Exception e) {
                log.warn("Search user failed", e);
                return new TableDataInfo<>(Collections.emptyList(), 0);
            }
        } else if ("video".equalsIgnoreCase(type) || "artist".equalsIgnoreCase(type)) {
            return new TableDataInfo<>(Collections.emptyList(), 0);
        }
        return new TableDataInfo<>(Collections.emptyList(), 0);
    }
}
