package com.hk.controller;


import com.hk.common.ResponseBuilder;
import com.hk.common.WebContext;
import com.hk.entity.TagAnnounceEntity;
import com.hk.service.TagService;
import com.hk.utils.MiscUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hkk
 * @since 2019-01-14
 */
@RestController
@RequestMapping("/tag")
public class TagController extends BaseController {

    @Autowired
    private TagService tagService;

    @RequestMapping("/list")
    private Object list() {
        String openId = checkLogin();
        return ResponseBuilder.renderList(tagService.list(openId));
    }

    @RequestMapping("/status")
    private Object status() {
        WebContext context = WebContext.get();
        String openId = checkLogin();
        long tagId = context.getRequiredLong("tagId");
        return ResponseBuilder.render("status", tagService.status(openId, tagId));
    }

    @RequestMapping("/msg-list")
    private Object msgList() {
        WebContext context = WebContext.get();
        String openId = checkLogin();
        long tagId = context.getRequiredLong("tagId");
        TagAnnounceEntity entity = tagService.getAnnounce(openId, tagId);
        return ResponseBuilder.create()
                .put("getAnnounce", MiscUtils.toMapInclude(entity, TagAnnounceEntity.TAG_ID, TagAnnounceEntity.CONTENT))
                .put("msgList", tagService.listMsg(entity.getId(), tagId))
                .build();
    }

    @RequestMapping("/add-msg")
    private Object addMsg() {
        WebContext context = WebContext.get();
        String openId = checkLogin();
        long tagId = context.getRequiredLong("tagId");
        String content = context.getRequiredParam("content");
        return ResponseBuilder.renderBoolean(tagService.addMsg(openId, tagId, content));
    }

    @RequestMapping("/bind")
    private Object bind() {
        WebContext context = WebContext.get();
        String openId = checkLogin();
        long tagId = context.getRequiredLong("tagId");
        checkUser(openId);
        return ResponseBuilder.renderBoolean(tagService.bind(openId, tagId));
    }

    @RequestMapping("/update-title")
    private Object updateTitle() {
        WebContext context = WebContext.get();
        String openId = checkLogin();
        checkUser(openId);
        long tagId = context.getRequiredLong("tagId");
        String title = context.getRequiredParam("title");
        return ResponseBuilder.renderBoolean(tagService.updateTitle(openId, tagId, title));
    }

    @RequestMapping("/update-getAnnounce")
    private Object updateAnnounce() {
        WebContext context = WebContext.get();
        String openId = checkLogin();
        checkUser(openId);
        long tagId = context.getRequiredLong("tagId");
        String announce = context.getRequiredParam("getAnnounce");
        return ResponseBuilder.renderBoolean(tagService.updateNotice(openId, tagId, announce));
    }
}
