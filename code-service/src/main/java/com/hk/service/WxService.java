package com.hk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hk.common.ResponseBuilder;
import com.hk.utils.CommonUtils;
import com.hk.utils.HttpClient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * Created by kang on 2019/1/8.
 */
@Service
public class WxService {

    @Autowired
    private WxUserService wxUserService;
    private static final String appId = "";
    private static final String secret = "";
    private static final String SESSION_KEY_API
            = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%d&grant_type"
            + "=authorization_code";

    public Object login(String code) {
        if (CommonUtils.isDevEnv()) {
            wxUserService.setOpenId(code, code, "sessionKey");
            return ResponseBuilder.create()
                    .put("login", true)
                    .put("openId", code)
                    .put("token", "token")
                    .build();
        }
        String url = String.format(SESSION_KEY_API, appId, secret, code);
        String content = HttpClient.httpGet(url);
        if (StringUtils.isNotBlank(content)) {
            JSONObject jo = JSON.parseObject(content);
            if (jo.containsKey("openid")) {
                String openid = jo.getString("openid");
                String sessionKey = jo.getString("session_key");
                String token = DigestUtils.md5DigestAsHex(sessionKey.getBytes());
                wxUserService.setOpenId(token, openid, sessionKey);
                return ResponseBuilder.create()
                        .put("login", true)
                        .put("openId", openid)
                        .put("token", token)
                        .build();
            } else {
                return ResponseBuilder.create().put("login", false).put("errmsg", jo.getString("errmsg")).build();
            }
        }
        return null;
    }
}
