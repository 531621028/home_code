package com.hk.dto;

/**
 * Created by kang on 2019/1/8.
 */
public class AuthCache {
    private String openId;
    private String sessionKey;

    public static AuthCache build(String openId, String sessionKey) {
        AuthCache authCache = new AuthCache();
        authCache.setOpenId(openId);
        authCache.setSessionKey(sessionKey);
        return authCache;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }
}
