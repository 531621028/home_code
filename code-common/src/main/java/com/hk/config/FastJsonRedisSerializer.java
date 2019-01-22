package com.hk.config;

import com.alibaba.fastjson.JSON;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

public class FastJsonRedisSerializer implements RedisSerializer {
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private final Charset charset = Charset.forName("UTF8");

    private boolean isEmpty(byte[] data) {
        return (data == null || data.length == 0);
    }

    @Override
    public String deserialize(byte[] bytes) {
        if (isEmpty(bytes)) {
            return null;
        }
        try {
            return new String(bytes, charset);
        } catch (Exception ex) {
            throw new SerializationException("Cannot deserialize", ex);
        }
    }

    @Override
    public byte[] serialize(Object t) {
        if (t == null) {
            return EMPTY_ARRAY;
        }
        try {
            if (t instanceof String) {
                return ((String) t).getBytes(charset);
            }
            return JSON.toJSONString(t).getBytes(charset);
        } catch (Exception ex) {
            throw new SerializationException("Cannot serialize", ex);
        }
    }
}
