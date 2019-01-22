package com.hk.utils;

/**
 * Created by kang on 2019/1/14.
 */
public class RedisKeyUtils {
    public static String buildTagCount(long tagId) {
        return String.format("tag:cnt:%d", tagId);
    }
}
