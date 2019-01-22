package com.hk.controller;

import com.hk.common.WebContext;
import com.hk.common.constants.GlobalConstants;
import com.hk.dto.AuthCache;
import com.hk.entity.WxUserEntity;
import com.hk.exception.ClientException;
import com.hk.service.WxUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by kang on 2019/1/8.
 */
@Service
public class BaseController {
    @Autowired
    private WxUserService wxUserService;

    protected String checkLogin() {
        WebContext context = WebContext.get();
        String token = context.getRequiredParam("token");
        AuthCache authCache = wxUserService.getAuth(token);
        if (authCache != null) {
            return authCache.getOpenId();
        } else {
            throw new ClientException("登录验证失败", GlobalConstants.ErrorCode.LOGIN_FAIL);
        }
    }

    protected void checkUser(String openId) {
        WxUserEntity wxUserEntity = wxUserService.getWxUser(openId);
        if (wxUserEntity == null) {
            wxUserService.insertOrUpdate(openId);
        }
    }
}
