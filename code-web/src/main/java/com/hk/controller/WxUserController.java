package com.hk.controller;


import com.hk.common.WebContext;
import com.hk.service.WxService;
import com.hk.service.WxUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信用户表 前端控制器
 * </p>
 *
 * @author hkk
 * @since 2019-01-09
 */
@RestController
@RequestMapping("/wx-user")
public class WxUserController extends BaseController {
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private WxService wxService;

    @RequestMapping("/view")
    public Object view() {
        WebContext context = WebContext.get();
        String openId = context.getRequiredParam("openId");
        return wxUserService.getWxUser(openId);
    }

    @RequestMapping("/login")
    public Object login() {
        WebContext context = WebContext.get();
        String code = context.getRequiredParam("code");
        return wxService.login(code);
    }

    @RequestMapping("/update")
    public boolean update() {
        String openId = checkLogin();
        return wxUserService.insertOrUpdate(openId);
    }

}
