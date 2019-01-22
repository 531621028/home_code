package com.hk.common;

import org.dozer.DozerBeanMapper;

/**
 * Created by kang on 2019/1/8.
 */
public class Singleton {
    private Singleton() {
    }

    /**
     * 由于静态内部类的特性，只有在其被第一次引用的时候才会被加载，所以可以保证其线程安全性。
     */
    private static class SingletonInstance {
        private static final DozerBeanMapper DOZER_BEAN_MAPPER = new DozerBeanMapper();
    }

    public static DozerBeanMapper getDozerBeanMapper() {
        return SingletonInstance.DOZER_BEAN_MAPPER;
    }
}
