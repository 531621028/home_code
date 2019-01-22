package com.hk.dto;

import com.hk.dto.count.TagCount;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author hkk
 * @since 2019-01-14
 */
public class Tag {

    private long tagId;
    /**
     * 微信用户的openId
     */
    private String openId;
    /**
     * 标签标题
     */
    private String title;
    /**
     * 标签二维码
     */
    private String qrCode;

    /**
     * 标签相关的技术
     */
    private TagCount tagCount;

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }

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

    public TagCount getTagCount() {
        return tagCount;
    }

    public void setTagCount(TagCount tagCount) {
        this.tagCount = tagCount;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
