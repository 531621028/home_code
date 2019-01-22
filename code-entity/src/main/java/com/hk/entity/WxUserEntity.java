package com.hk.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * <p>
 * 微信用户表
 * </p>
 *
 * @author hkk
 * @since 2019-01-09
 */
@TableName("wx_user")
public class WxUserEntity extends IdEntity {

    private static final long serialVersionUID = -4457477600820795057L;
    /**
     * 微信的openId
     */
    private String openId;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 所在省份
     */
    private String country;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所在省份
     */
    private String city;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 最后活跃时间
     */
    private LocalDateTime activteTime;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public LocalDateTime getActivteTime() {
        return activteTime;
    }

    public void setActivteTime(LocalDateTime activteTime) {
        this.activteTime = activteTime;
    }

    public static final String OPEN_ID = "open_id";

    public static final String AVATAR_URL = "avatar_url";

    public static final String NICK_NAME = "nick_name";

    public static final String GENDER = "gender";

    public static final String COUNTRY = "country";

    public static final String PROVINCE = "province";

    public static final String CITY = "city";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

    public static final String ACTIVTE_TIME = "activte_time";

    @Override
    public String toString() {
        return "WxUserEntity{" +
        "openId=" + openId +
        ", avatarUrl=" + avatarUrl +
        ", nickName=" + nickName +
        ", gender=" + gender +
        ", country=" + country +
        ", province=" + province +
        ", city=" + city +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", activteTime=" + activteTime +
        "}";
    }
}
