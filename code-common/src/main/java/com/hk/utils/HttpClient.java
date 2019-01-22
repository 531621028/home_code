package com.hk.utils;

import com.hk.common.NameAndValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kang on 2019/1/8.
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
    private static final OkHttpClient DEFAULT = new OkHttpClient.Builder()
            .readTimeout(20, TimeUnit.SECONDS).connectTimeout(6, TimeUnit.SECONDS).writeTimeout(60, TimeUnit
                    .SECONDS).build();


    public static String httpGet(String url) {
        return httpGet(url, new ArrayList<>());
    }

    public static String httpGet(String url, List<NameAndValue> params) {
        String URL = buildUrl(url, params);
        Request request = new Request.Builder()
                .url(URL)
                .build();
        Response response;
        try {
            response = DEFAULT.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            LOG.error("请求失败", e);
        }
        return null;
    }

    private static String buildUrl(String url, List<NameAndValue> params) {
        if (url.contains("?")) {
            return url + "&" + toParamsString(params);
        } else {
            return url + "?" + toParamsString(params);
        }
    }


    private static String toParamsString(List<NameAndValue> params) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (NameAndValue param : params) {
            if (i++ == 0) {
                sb.append(param.toParam());
            } else {
                sb.append("&").append(param.toParam());
            }
        }
        return sb.toString();
    }
}
