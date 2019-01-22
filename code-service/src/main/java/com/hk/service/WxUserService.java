package com.hk.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hk.common.WebContext;
import com.hk.dao.WxUserEntityDao;
import com.hk.dto.AuthCache;
import com.hk.entity.WxUserEntity;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by kang on 2019/1/7.
 */
@Service
public class WxUserService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private WxUserEntityDao wxUserEntityDao;

    private LoadingCache<String, Optional<AuthCache>> tokenCache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Optional<AuthCache>>() {
                        @Override
                        public Optional<AuthCache> load(String key) throws Exception {
                            String value = redisService.get(key);
                            if (StringUtils.isNotBlank(value)) {
                                AuthCache authCache = JSON.parseObject(value, AuthCache.class);
                                return Optional.of(authCache);
                            }
                            return Optional.empty();
                        }
                    });

    private LoadingCache<String, Optional<WxUserEntity>> wxUserCache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Optional<WxUserEntity>>() {
                        @Override
                        public Optional<WxUserEntity> load(String key) throws Exception {
                            QueryWrapper<WxUserEntity> qw = new QueryWrapper<>();
                            qw.eq(WxUserEntity.OPEN_ID, key);
                            WxUserEntity entity = wxUserEntityDao.selectOne(qw);
                            if (entity == null) {
                                return Optional.empty();
                            }
                            return Optional.of(entity);
                        }
                    });

    public AuthCache getAuth(String token) {
        return tokenCache.getUnchecked(token).orElse(null);
    }

    public void setOpenId(String token, String openId, String sessionKey) {
        AuthCache authCache = AuthCache.build(openId, sessionKey);
        tokenCache.put(token, Optional.of(authCache));
        redisService.expire(token, 1, TimeUnit.HOURS);
    }

    public WxUserEntity getWxUser(String openId) {
        return wxUserCache.getUnchecked(openId).orElse(null);
    }

    public void refresh(String openId) {
        wxUserCache.refresh(openId);
    }

    public boolean insertOrUpdate(String openId) {
        WebContext context = WebContext.get();
        WxUserEntity wxUserEntity = getWxUser(openId);
        if (wxUserEntity == null) {
            wxUserEntity = new WxUserEntity();
            wxUserEntity.setCreateTime(LocalDateTime.now());
        }
        wxUserEntity.setOpenId(openId);
        wxUserEntity.setAvatarUrl(context.getRequiredParam("avatarUrl"));
        wxUserEntity.setNickName(context.getRequiredParam("nickName"));
        wxUserEntity.setGender(context.getRequiredInt("gender"));
        wxUserEntity.setActivteTime(LocalDateTime.now());
        wxUserEntity.setCity(context.getParam("city"));
        wxUserEntity.setProvince(context.getParam("province"));
        wxUserEntity.setCountry(context.getParam("country"));
        wxUserEntity.setUpdateTime(LocalDateTime.now());
        boolean success;
        if (wxUserEntity.getId() != null) {
            success = wxUserEntityDao.updateById(wxUserEntity) == 1;
        } else {
            success = wxUserEntityDao.insert(wxUserEntity) == 1;
        }
        if (success) {
            refresh(openId);
        }
        return success;
    }
}
