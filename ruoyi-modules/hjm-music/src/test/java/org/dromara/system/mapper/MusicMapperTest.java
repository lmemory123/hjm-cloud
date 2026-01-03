package org.dromara.system.mapper;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.dromara.system.domain.Music;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.dromara.system.domain.table.MusicTableDef.MUSIC;


/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: TokyoMomao
 * DateTime: 2025-12-22 13:52
 */
@SpringBootTest
public class MusicMapperTest {

    @Resource
    private MusicMapper musicMapper;

    @Test
    @Order(1)
    void testAdd(){
        // 创建音乐对象
        Music music = new Music();
        music.setTitle("测试歌曲标题");
        music.setSubtitle("测试副标题");
        music.setOriginalTitle("测试原曲名");
        music.setCreatorId(1L);
        music.setCreatorName("测试用户");
        music.setCreatorLink("https://example.com");
        music.setProducerMark("全民制作人");
        music.setDuration(180L);
        music.setBpm(120L);
        music.setPublishTime(new Date());
        music.setPlayCount(0L);
        music.setLikeCount(0L);
        music.setCollectCount(0L);
        music.setCommentCount(0L);
        music.setShareCount(0L);
        music.setDownloadCount(0L);
        music.setAuditStatus("0");
        music.setIsPublic("1");
        music.setIsOriginal("0");
        music.setResourceStatus("0");
        music.setCopyrightInfo("测试版权信息");
        music.setRemark("测试备注");
        music.setDelFlag("0");

        // 执行插入
        int result = musicMapper.insert(music);

        // 验证结果
        System.out.println("插入结果: " + result);
        System.out.println("生成的ID: " + music.getId());

        // 断言插入成功
        assert result > 0;
        assert music.getId() != null;
    }

    @Test
    @Order(2)
    void testQuery(){
        // 查询所有音乐
        Music music1 = new Music();
        music1.setTitle(null);
        music1.setCreatorName("");


        QueryWrapper where = QueryWrapper.create().where(MUSIC.TITLE.eq(music1.getTitle()).and(MUSIC.CREATOR_NAME.like(music1.getCreatorName())));


        System.out.println("生成的SQL: " + where.toSQL());
        System.out.println("========================");
        List<Music> musicList = musicMapper.selectListByQuery(where);

        // 打印结果
        System.out.println("查询到的音乐数量: " + musicList.size());
        for (Music music : musicList) {
            System.out.println("ID: " + music.getId() + ", 标题: " + music.getTitle());
        }

        // 断言不为空
        assert musicList != null;
    }

    @Test
    @Order(3)
    void testQueryById(){
        // 先插入一条数据
        Music music = new Music();
        music.setTitle("查询测试歌曲");
        music.setCreatorId(1L);
        music.setCreatorName("测试用户");
        music.setAuditStatus("0");
        music.setIsPublic("1");
        music.setIsOriginal("0");
        music.setResourceStatus("0");
        music.setDelFlag("0");
        musicMapper.insert(music);

        // 根据ID查询
        Music queriedMusic = musicMapper.selectOneById(music.getId());

        // 打印结果
        System.out.println("查询结果 - ID: " + queriedMusic.getId() + ", 标题: " + queriedMusic.getTitle());

        // 断言查询成功
        assert queriedMusic != null;
        assert queriedMusic.getTitle().equals("查询测试歌曲");
    }
}
