package com.hk.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author hkk
 * @since 2019-01-14
 */
@TableName("tag")
public class TagEntity extends IdEntity {
    /**
     * 微信用户的openId
     */
    private String openId;

    /**
     * 标签长度
     */
    private String title;

    /**
     * 标签二维码
     */
    private String qrCode;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public static final String OPEN_ID = "open_id";

    public static final String QR_CODE = "qr_code";

    public static final String TITLE = "title";

    public static final String CREATE_TIME = "create_time";

    @Override
    public String toString() {
        return "TagEntity{" +
        "openId=" + openId +
        ", title=" + title +
        ", createTime=" + createTime +
        "}";
    }
}
