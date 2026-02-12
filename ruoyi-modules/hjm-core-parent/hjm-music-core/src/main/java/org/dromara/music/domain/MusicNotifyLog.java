package org.dromara.music.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatisflex.core.domain.QueryBaseEntity;

import java.util.Date;


/**
 * 音乐通知日志对象 music_notify_log
 *
 * @author momao
 * @date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("music_notify_log")
public class MusicNotifyLog extends QueryBaseEntity {


    /**
     * 主键
     */
    @Id
    private Long id;

    /**
     * 音乐ID
     */
    private Long musicId;

    /**
     * 接收用户ID
     */
    private Long userId;

    /**
     * 通知类型
     */
    private String notifyType;

    /**
     * 通知标题
     */
    private String notifyTitle;

    /**
     * 通知内容
     */
    private String notifyContent;

    /**
     * 发送渠道
     */
    private String sendChannel;

    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 阅读状态
     */
    private String readStatus;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 创建时间
     */
    private Date createTime;


}
