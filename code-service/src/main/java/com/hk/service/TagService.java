package com.hk.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hk.constants.TagConstants;
import com.hk.dao.TagAnnounceEntityDao;
import com.hk.dao.TagEntityDao;
import com.hk.dao.TagMsgEntityDao;
import com.hk.dto.Tag;
import com.hk.entity.TagAnnounceEntity;
import com.hk.entity.TagEntity;
import com.hk.entity.TagMsgEntity;
import com.hk.exception.ClientException;
import com.hk.service.common.RenderService;
import com.hk.utils.BeanUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by kang on 2019/1/14.
 */
@Service
public class TagService {

    @Autowired
    private TagEntityDao tagEntityDao;
    @Autowired
    private TagAnnounceEntityDao tagAnnounceEntityDao;
    @Autowired
    private TagMsgEntityDao tagMsgEntityDao;
    @Autowired
    private RenderService renderService;
    private LoadingCache<Long, Optional<Tag>> cache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<Long, Optional<Tag>>() {
                        @Override
                        public Optional<Tag> load(Long key) throws Exception {
                            TagEntity entity = tagEntityDao.selectById(key);
                            if (entity == null) {
                                return Optional.empty();
                            }
                            Tag tag = BeanUtils.copy(entity, Tag.class);
                            tag.setTagId(key);
                            return Optional.of(tag);
                        }
                    });

    public Tag get(long tagId) {
        return cache.getUnchecked(tagId).orElse(null);
    }

    public void refresh(long tagId) {
        cache.refresh(tagId);
    }

    public List<Tag> list(String openId) {
        QueryWrapper<TagEntity> qw = new QueryWrapper<>();
        qw.select(TagEntity.ID);
        qw.eq(TagEntity.OPEN_ID, openId);
        List<TagEntity> entityList = tagEntityDao.selectList(qw);
        return entityList.stream().map(e -> {
            Tag tag = get(e.getId());
            tag.setTagCount(renderService.renderTagCount(e.getId()));
            return tag;
        }).collect(Collectors.toList());
    }

    public boolean bind(String openId, long tagId) {
        boolean success;
        TagEntity entity = tagEntityDao.selectById(tagId);
        if (entity == null) {
            entity = build(openId, tagId);
            success = tagEntityDao.insert(entity) >= 1;
        } else {
            if (StringUtils.isNotBlank(entity.getOpenId())) {
                throw new ClientException("该标签已经被绑定");
            } else {
                entity.setOpenId(openId);
                entity.setTitle("我的标签");
            }
            success = tagEntityDao.updateById(entity) >= 1;
        }
        if (success) {
            refresh(tagId);
        }
        return success;
    }

    private TagEntity build(String openId, long tagId) {
        TagEntity entity = new TagEntity();
        entity.setId(tagId);
        entity.setOpenId(openId);
        entity.setTitle("我的标签");
        entity.setCreateTime(LocalDateTime.now());
        return entity;
    }

    public int status(String openId, long tagId) {
        TagEntity entity = tagEntityDao.selectById(tagId);
        if (entity == null || StringUtils.isBlank(entity.getOpenId())) {
            return TagConstants.TagStatus.NONE;
        } else if (entity.getOpenId().equals(openId)) {
            return TagConstants.TagStatus.OWNED;
        } else {
            return TagConstants.TagStatus.OTHER;
        }
    }


    public boolean updateTitle(String openId, long tagId, String title) {
        TagEntity entity = tagEntityDao.selectById(tagId);
        if (entity == null) {
            throw new ClientException("该标签不存在");
        } else if (!openId.equals(entity.getOpenId())) {
            throw new ClientException("不能修改别人的标签");
        } else {
            entity.setTitle(title);
            boolean success = tagEntityDao.updateById(entity) >= 1;
            if (success) {
                refresh(tagId);
            }
            return success;
        }
    }

    public boolean updateNotice(String openId, long tagId, String notice) {
        QueryWrapper<TagAnnounceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TagAnnounceEntity.TAG_ID, tagId);
        queryWrapper.eq(TagAnnounceEntity.STATUS, TagConstants.AnnounceStatus.VALID);
        TagAnnounceEntity tagAnnounceEntity = tagAnnounceEntityDao.selectOne(queryWrapper);
        if (tagAnnounceEntity != null) {
            tagAnnounceEntity.setStatus(TagConstants.AnnounceStatus.INVALID);
            tagAnnounceEntity.setUpdateTime(LocalDateTime.now());
            tagAnnounceEntityDao.updateById(tagAnnounceEntity);
        }
        tagAnnounceEntity = new TagAnnounceEntity();
        tagAnnounceEntity.setOpenId(openId);
        tagAnnounceEntity.setContent(notice);
        tagAnnounceEntity.setTagId(tagId);
        tagAnnounceEntity.setStatus(TagConstants.AnnounceStatus.VALID);
        tagAnnounceEntity.setCreateTime(LocalDateTime.now());
        tagAnnounceEntity.setUpdateTime(LocalDateTime.now());
        return tagAnnounceEntityDao.insert(tagAnnounceEntity) > 0;
    }

    public TagAnnounceEntity getAnnounce(String openId, long tagId) {
        QueryWrapper<TagAnnounceEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TagAnnounceEntity.TAG_ID, tagId);
        queryWrapper.eq(TagAnnounceEntity.OPEN_ID, openId);
        queryWrapper.eq(TagAnnounceEntity.STATUS, TagConstants.AnnounceStatus.VALID);
        TagAnnounceEntity tagAnnounceEntity = tagAnnounceEntityDao.selectOne(queryWrapper);
        if (tagAnnounceEntity != null) {
            return tagAnnounceEntity;
        }
        throw new ClientException("没有发布公告");
    }

    public List<Map<String, Object>> listMsg(long annId, long tagId) {
        QueryWrapper<TagMsgEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TagMsgEntity.TAG_ID, tagId);
        queryWrapper.eq(TagMsgEntity.ANNOUNCE_ID, annId);
        List<TagMsgEntity> tagMsgEntityList = tagMsgEntityDao.selectList(queryWrapper);
        return tagMsgEntityList.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put(TagMsgEntity.CONTENT, e.getContent());
            map.put(TagMsgEntity.CREATE_TIME, e.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public boolean addMsg(String openId, long tagId, String content) {
        Tag tag = get(tagId);
        if (tag == null) {
            throw new ClientException("标签不存在");
        }
        TagAnnounceEntity announceEntity = getAnnounce(openId, tagId);
        if (announceEntity == null) {
            throw new ClientException("当前标签不能留言");
        }
        TagMsgEntity msgEntity = new TagMsgEntity();
        msgEntity.setAnnounceId(announceEntity.getId());
        msgEntity.setContent(content);
        msgEntity.setTagId(tagId);
        if (openId.equals(tag.getOpenId())) {
            msgEntity.setType(TagConstants.MsgType.MINE);
        } else {
            msgEntity.setType(TagConstants.MsgType.OTHER);
        }
        msgEntity.setCreateTime(LocalDateTime.now());
        return tagMsgEntityDao.insert(msgEntity) > 0;

    }
}
