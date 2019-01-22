package com.hk.service.count;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import com.hk.dto.count.TagCount;
import com.hk.service.RedisService;
import com.hk.utils.BeanUtils;
import com.hk.utils.RedisKeyUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

/**
 * Created by kang on 2019/1/14.
 */
@Service
public class CountService {
    @Autowired
    private RedisService redisService;
    private LoadingCache<Long, TagCount> tagCountCache;

    @PostConstruct
    void init() {
        tagCountCache = CacheBuilder.newBuilder()
                .recordStats()
                .maximumSize(1000)
                .initialCapacity(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<Long, TagCount>() {
                    @Override
                    public TagCount load(Long clubId) throws Exception {
                        TagCount cc = new TagCount();
                        Map<Object, Object> data = redisService.entries(RedisKeyUtils.buildTagCount(clubId));
                        if (data != null) {
                            BeanUtils.copy(data, cc);
                        }
                        return cc;
                    }
                });
    }

    public TagCount getTagCount(long tagId) {
        return tagCountCache.getUnchecked(tagId);
    }

    public void updateTagCount(long tagId, long delta) {
        tagCountCache.getUnchecked(tagId).updateUrCnt(tagId, 1);
        redisService.hashIncrement(RedisKeyUtils.buildTagCount(tagId), TagCount.Field.tCnt, delta);
        tagCountCache.getUnchecked(tagId).updateUrCnt(tagId, 1);
        redisService.hashIncrement(RedisKeyUtils.buildTagCount(tagId), TagCount.Field.urCnt, delta);
    }
}
