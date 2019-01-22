package com.hk.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author hkk
 * @since 2019-01-16
 */
@TableName("tag_msg")
public class TagMsgEntity extends IdEntity {

    /**
     * 标签id
     */
    private Long tagId;

    /**
     * 公告的id
     */
    private Long announceId;

    /**
     * 微信用户的openId
     */
    private String openId;

    /**
     * 留言消息
     */
    private String content;

    /**
     * 留言的类型
     */
    private Integer type;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
    public Long getAnnounceId() {
        return announceId;
    }

    public void setAnnounceId(Long announceId) {
        this.announceId = announceId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public static final String TAG_ID = "tag_id";

    public static final String ANNOUNCE_ID = "announce_id";

    public static final String OPEN_ID = "open_id";

    public static final String CONTENT = "content";

    public static final String TYPE = "type";

    public static final String CREATE_TIME = "create_time";

    @Override
    public String toString() {
        return "TagMsgEntity{" +
        "tagId=" + tagId +
        ", announceId=" + announceId +
        ", openId=" + openId +
        ", content=" + content +
        ", type=" + type +
        ", createTime=" + createTime +
        "}";
    }
}
