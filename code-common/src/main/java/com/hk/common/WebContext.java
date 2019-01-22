package com.hk.common;

import com.google.common.base.Splitter;

import com.hk.exception.ClientException;
import com.hk.utils.MiscUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class WebContext {

    private static final Logger LOG = LoggerFactory.getLogger(WebContext.class);
    private static final ThreadLocal<WebContext> threadLocal = new ThreadLocal<>();

    /**
     * response code 304
     */
    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String FORMAT_YYYYMMDD = "yyyyMMdd";

    private HttpServletRequest request;
    private HttpServletResponse response;

    public static WebContext get() {
        return threadLocal.get();
    }

    public static void set(WebContext context) {
        threadLocal.set(context);
    }

    public static void remove() {
        threadLocal.remove();
    }


    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public String getRequiredParam(String name) {
        return getRequiredParam(name, null);
    }


    /**
     * 支持自定义错误提示
     *
     * @param message 错误提示信息，不可以为空
     */
    public String getRequiredParam(String name, String message) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            if (StringUtils.isNotBlank(message)) {
                throw new ClientException(message, com.hk.common.constants.GlobalConstants.ErrorCode.ILLEGAL_PARAM_CODE);
            } else {
                throw new ClientException("参数:" + name + " 不能为空", com.hk.common.constants.GlobalConstants.ErrorCode.ILLEGAL_PARAM_CODE);
            }
        }
        return value;
    }

    public List<Long> getRequiredLongList(String name) {
        return getRequiredLongList(name, null);
    }

    public List<Long> getRequiredLongList(String name, String message) {
        String value = getRequiredParam(name, message);
        List<Long> list = new ArrayList<>();
        String[] ss = value.split(",");
        for (String s : ss) {
            list.add(Long.valueOf(s));
        }
        return list;
    }

    public int getRequiredDateInt(String name) {
        return getRequiredDateInt(name, null);
    }

    public int getRequiredDateInt(String name, String message) {
        Date date = getRequiredDate(name, message);
        return NumberUtils.toInt(MiscUtils.formatDate(date, FORMAT_YYYYMMDD));
    }

    public long getRequiredDateLong(String name) {
        return getRequiredDateLong(name, null);
    }

    public long getRequiredDateLong(String name, String message) {
        Date date = getRequiredDate(name, message);
        return NumberUtils.toLong(MiscUtils.formatDate(date, FORMAT_YYYYMMDDHHMMSS));
    }

    public Date getRequiredDate(String name) {
        return getRequiredDate(name, null);
    }

    public Date getRequiredDate(String name, String message) {
        return MiscUtils.parseDate(getRequiredParam(name, message), FORMAT_YYYY_MM_DD, FORMAT_YYYY_MM_DD_HH_MM_SS,
                FORMAT_YYYY_MM_DD_HH_MM);
    }

    public boolean getRequiredBoolean(String name) {
        return getRequiredBoolean(name, null);
    }

    public boolean getRequiredBoolean(String name, String message) {
        return MiscUtils.toBoolean(getRequiredParam(name, message));
    }

    public double getRequiredDouble(String name) {
        return getRequiredDouble(name, null);
    }

    public double getRequiredDouble(String name, String message) {
        return NumberUtils.toDouble(getRequiredParam(name, message));
    }

    public float getRequiredFloat(String name) {
        return getRequiredFloat(name, null);
    }

    public float getRequiredFloat(String name, String message) {
        return NumberUtils.toFloat(getRequiredParam(name, message));
    }

    public int getRequiredInt(String name) {
        return getRequiredInt(name, null);
    }

    public int getRequiredInt(String name, String message) {
        return NumberUtils.toInt(getRequiredParam(name, message));
    }

    public long getRequiredLong(String name) {
        return getRequiredLong(name, null);
    }

    public long getRequiredLong(String name, String message) {
        return NumberUtils.toLong(getRequiredParam(name, message));
    }

    public String getParam(String name) {
        return MiscUtils.getParam(request, name);
    }

    public String getParam(String name, String defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 返回字符串列表
     *
     * @param name         请求参数名称
     * @param defaultValue 默认数据
     * @return 字符串列表
     */
    public List<String> getParamStringList(String name, String defaultValue) {
        return Splitter.on(",").omitEmptyStrings().splitToList(getParam(name, defaultValue));
    }

    public boolean getParam(String name, boolean defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return MiscUtils.toBoolean(value);
    }

    public List<Long> getParam(String name, List<Long> defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        List<Long> list = new ArrayList<>();
        String[] ss = value.split(",");
        for (String s : ss) {
            list.add(Long.valueOf(s));
        }
        return list;
    }

    public double getParam(String name, double defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return NumberUtils.toDouble(value, defaultValue);
    }

    public int getParamDate(String name, int defaultValue) {
        Date date = getParamDate(name);
        if (date == null) {
            return defaultValue;
        }
        return NumberUtils.toInt(MiscUtils.formatDate(date, FORMAT_YYYYMMDD), defaultValue);
    }

    public long getParamDateTime(String name, long defaultValue) {
        Date date = getParamDate(name);
        if (date == null) {
            return defaultValue;
        }
        return NumberUtils.toLong(MiscUtils.formatDate(date, FORMAT_YYYYMMDDHHMMSS), defaultValue);
    }

    public Date getParamDate(String name) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return MiscUtils.parseDate(value, FORMAT_YYYY_MM_DD, FORMAT_YYYY_MM_DD_HH_MM_SS, FORMAT_YYYY_MM_DD_HH_MM);
    }

    public float getParam(String name, float defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return NumberUtils.toFloat(value, defaultValue);
    }

    public int getParam(String name, int defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return NumberUtils.toInt(value, defaultValue);
    }

    public long getParam(String name, long defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return NumberUtils.toLong(value, defaultValue);
    }


    public Object convert(Class<?> cls, String value) {
        Object t = value;
        try {
            if (cls == String.class) {
                t = value;
            } else if (Date.class.isAssignableFrom(cls)) {
                t = DateUtils.parseDate(value, FORMAT_YYYY_MM_DD, FORMAT_YYYY_MM_DD_HH_MM_SS);
            } else if (cls == Boolean.class || cls == boolean.class) {
                return MiscUtils.toBoolean(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return t;
    }

    public long getId() {
        long id = getParam("id", -1L);
        if (id <= 0) {
            throw new ClientException("参数ID不能为空", com.hk.common.constants.GlobalConstants.ErrorCode.ILLEGAL_PARAM_CODE);
        }
        return id;
    }

    public PageRequest page() {
        PageRequest page = new PageRequest();
        page.setPage(getParam("page", 1));
        page.setLimit(getParam("limit", 25));
        return page;
    }


}
