package com.hk.common;

import com.google.common.collect.Maps;

import com.alibaba.fastjson.JSON;
import com.hk.utils.MiscUtils;

import java.util.Collection;
import java.util.Map;

/**
 * @since 2016-05-24
 */
public class ResponseBuilder {
    private Map<String, Object> map;

    private ResponseBuilder() {
        this.map = Maps.newHashMap();
    }

    public static ResponseBuilder create(Object obj) {
        ResponseBuilder builder = new ResponseBuilder();
        if (obj != null) {
            builder.map.putAll(MiscUtils.toMapExclude(obj));
        }
        return builder;
    }

    public static ResponseBuilder create() {
        return new ResponseBuilder();
    }

    public ResponseBuilder put(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    public Map<String, Object> build() {
        return this.map;
    }

    /**
     * 转换成JSON字符串
     *
     * @return
     */
    public String toJson() {
        return JSON.toJSONString(map);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(map);
    }

    public static Map<String, Object> render(String name, Object value) {
        return ResponseBuilder.create()
                .put(name, value)
                .build();
    }

    public static Map<String, Object> renderList(Collection<?> itemList) {
        return ResponseBuilder.create()
                .put("itemList", itemList)
                .build();
    }

    public static JsonResult renderBoolean(boolean success) {
        JsonResult ret = new JsonResult();
        ret.setData(Maps.newHashMap());
        ret.setSuccess(success);
        return ret;
    }
}
