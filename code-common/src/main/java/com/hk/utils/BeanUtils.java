package com.hk.utils;


import com.hk.common.Singleton;

import org.dozer.DozerBeanMapper;

/**
 * 实体工具类
 */
public class BeanUtils {
    private static DozerBeanMapper BEAN_MAPPER = Singleton.getDozerBeanMapper();

    public static void copy(Object source, Object dest) {
        if (source == null || dest == null) {
            return;
        }
        BEAN_MAPPER.map(source, dest);
    }

    public static <T> T copy(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        return BEAN_MAPPER.map(source, clazz);
    }

    public static <T> T map(Object source, Class<T> clazz) {
        return BEAN_MAPPER.map(source, clazz);
    }

}
