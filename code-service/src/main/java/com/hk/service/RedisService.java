package com.hk.service;

import com.alibaba.fastjson.JSON;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by kang on 2019/1/8.
 */
@Service
public class RedisService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void delete(String key) {
        stringRedisTemplate.opsForValue().getOperations().delete(key);
    }

    public void set(String key, String value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * @param key
     * @return
     */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(value)) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    /**
     * @param key
     * @param value
     */
    public void setObject(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    /**
     * @param keys
     * @return
     */
    public List<String> get(Collection<String> keys) {
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    public void hashSet(String key, String hashKey, String value) {
        stringRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    public void hashSetAll(String key, Map<String, Object> value) {
        stringRedisTemplate.opsForHash().putAll(key, value);
    }

    public String hashGet(String key, String hashKey) {
        Object object = stringRedisTemplate.opsForHash().get(key, hashKey);
        return null == object ? null : (String) object;
    }

    public List<Object> hashGetAll(String key) {
        return stringRedisTemplate.opsForHash().values(key);
    }

    public Map<Object, Object> hashGetAllEntries(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    public void hashDel(String key, String... hasKeys) {
        stringRedisTemplate.opsForHash().delete(key, hasKeys);
    }

    public long hashIncrement(String key, String hashKey, long increment) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, increment);
    }

    public double hashIncrement(String key, String hashKey, double increment) {
        return stringRedisTemplate.opsForHash().increment(key, hashKey, increment);
    }

    public void lrem(String key, String value) {
        stringRedisTemplate.opsForList().remove(key, 0, value);
    }

    public List<String> lrange(String key, int start, int end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    public long lpush(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    public long rpush(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public String lpop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public String rpop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public boolean zAdd(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().add(key, value, score);
    }

    public double zIncrement(String key, String value, double score) {
        return stringRedisTemplate.opsForZSet().incrementScore(key, value, score);
    }

    public void zRemove(String key, String... value) {
        stringRedisTemplate.opsForZSet().remove(key, value);
    }

    public double zScore(String key, String value) {
        Double ret = stringRedisTemplate.opsForZSet().score(key, value);
        return ret == null ? 0 : ret;
    }

    public long zRank(String key, String value, boolean reverse) {
        Long ret = reverse
                   ? stringRedisTemplate.opsForZSet().reverseRank(key, value)
                   : stringRedisTemplate.opsForZSet().rank(key, value);
        //排名是从0开始的
        return ret == null ? 0 : ret + 1;
    }

    public Set<String> zRange(String key, long start, long end, boolean reverse) {
        return reverse
               ? stringRedisTemplate.opsForZSet().reverseRange(key, start, end)
               : stringRedisTemplate.opsForZSet().range(key, start, end);
    }

    public Set<ZSetOperations.TypedTuple<String>> zrangeWithScore(String key, long start, long end, boolean reverse) {
        return reverse
               ? stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)
               : stringRedisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }

    /**
     * @param key
     */
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * @param keys
     */
    public void del(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    /**
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);

    }

    /**
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 针对redis incr命令的封装，实现指定key的值自增长
     *
     * @param key key值
     * @return 自增长后的值
     */
    public long incr(final String key) {
        long result = (long) stringRedisTemplate
                .execute((RedisCallback) connection -> connection.incr(key.getBytes()));
        return result;
    }

    public void expire(String key, int timeout, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, timeout, timeUnit);
    }

    public Map<Object, Object> entries(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }
}
