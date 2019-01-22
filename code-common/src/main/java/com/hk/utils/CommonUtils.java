package com.hk.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by kang on 2019/1/8.
 */
public class CommonUtils {
    /**
     * 是否开发环境
     */
    public static boolean isDevEnv() {
        String env = getEnv();
        return StringUtils.contains(env, "dev");
    }

    public static String getEnv() {
        return System.getProperty("spring.profiles.active");
    }
}
