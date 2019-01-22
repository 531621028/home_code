/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hk.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 一些不属于某些分类的，杂项工具类 如果不好确认分类，都可以放在此类中
 */
public class MiscUtils {
    public static final String VALID_CHARS_16 = "0123456789abcdef";
    public static final String VALID_CHARS_62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final long SECOND = 1000;
    public static final long MINUTE = 60000; //60 * SECOND;
    public static final long HOUR = 3600000;//60 * MINUTE;
    public static final long DAY = 86400000; //24 * HOUR;
    public static final String ACCOUNT_REGEX = "([\\w\\-.]+@([0-9a-zA-Z_\\-]+\\.)+[a-zA-Z]+)|(1[3458]\\d{9})";
    //放个常量进来
    public static final String UTF_8 = "UTF-8";
    public static final Charset CHARSET_UTF_8 = Charset.forName(UTF_8);
    /**
     * (标准模式)STANDARDPATTERN="yyyy-MM-dd HH:mm:ss"
     */
    public static final String STANDARDPATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final Logger LOG = LoggerFactory.getLogger(MiscUtils.class);
    private static final BigInteger BASE = new BigInteger("10000");

    private static CountDownLatch latch = new CountDownLatch(1);


    private MiscUtils() {
    }

    /**
     * 把一个整数的字符串转成一个带进制的字符串
     *
     * @param validChars 合法的字制字符串
     * @param number 数字,必须是整数
     * @return 带进制的字符串
     */
    public static String numberToString(String validChars, String number) {
        if (StringUtils.isBlank(number)) {
            throw new NumberFormatException(number);
        }
        BigInteger bi = new BigInteger(number);
        BigInteger result = bi.divide(new BigInteger(String.valueOf(validChars.length())));
        BigInteger rest = bi.remainder(new BigInteger(String.valueOf(validChars.length())));
        StringBuilder sb = new StringBuilder();
        toStringInternal(sb, validChars, result, rest);
        return sb.toString();
    }

    private static void toStringInternal(StringBuilder sb, String validChars, BigInteger s, BigInteger remainder) {
        if (s.equals(BigInteger.ZERO) && remainder.equals(BigInteger.ZERO)) {
            return;
        }
        char c = validChars.charAt(remainder.intValue());
        sb.insert(0, c);
        BigInteger result = s.divide(new BigInteger(String.valueOf(validChars.length())));
        BigInteger rest = s.remainder(new BigInteger(String.valueOf(validChars.length())));
        toStringInternal(sb, validChars, result, rest);
    }

    /**
     * 把字符串变成一个数字,根据传入的合法进制
     *
     * @param validChars 合法的进制字符串
     * @param string 进制的字符串
     * @return 数字的字符串表达法
     */
    public static String stringToNumber(String validChars, String string) {
        if (StringUtils.isBlank(string)) {
            throw new NumberFormatException(string);
        }
        BigInteger bi = BigInteger.ZERO;
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            int index = validChars.indexOf(c);
            if (index == -1) {
                throw new NumberFormatException(string);
            }
            int pow = string.length() - i - 1;
            BigInteger temp = new BigInteger(String.valueOf(validChars.length())).pow(pow)
                    .multiply(new BigInteger(String.valueOf(index)));
            bi = bi.add(temp);
        }
        return bi.toString();
    }

    public static void sleepQuiet(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    public static void applicationStartFinished() {
        latch.countDown();
    }

    public static void waitLatch() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            LOG.error(null, e);
        }
    }


    @Deprecated
    public static void checkFieldExistsInclude(Object value, String... fields) throws Exception {
        Field[] fs = value.getClass().getDeclaredFields();
        for (Field f : fs) {
            if (ArrayUtils.contains(fields, f.getName())) {
                f.setAccessible(true);
                Object obj = f.get(value);
                if (obj == null) {
                    throw new IllegalArgumentException(f.getName() + "不能为空");
                }
            }

        }
    }

    @Deprecated
    public static void checkFieldExistsExclude(Object value, String... ignoreFields) throws Exception {
        Field[] fs = value.getClass().getDeclaredFields();
        for (Field f : fs) {
            if (ArrayUtils.contains(ignoreFields, f.getName())) {
                continue;
            }
            f.setAccessible(true);
            Object obj = f.get(value);
            if (obj == null) {
                throw new IllegalArgumentException(f.getName() + "不能为空");
            }
        }
    }

    @Deprecated
    public static void checkFieldNotBlankInclude(Object value, String... fields) throws Exception {
        Field[] fs = value.getClass().getDeclaredFields();
        for (Field f : fs) {
            if (ArrayUtils.contains(fields, f.getName())) {
                f.setAccessible(true);
                Object obj = f.get(value);
                if (obj == null || (obj instanceof String && StringUtils.isBlank(obj.toString()))) {
                    throw new IllegalArgumentException(f.getName() + "不能为空");
                }
            }
        }
    }

    @Deprecated
    public static void checkFieldNotBlankExclude(Object value, String... ignoreFields) throws Exception {
        Field[] fs = value.getClass().getDeclaredFields();
        for (Field f : fs) {
            if (ArrayUtils.contains(ignoreFields, f.getName())) {
                continue;
            }
            f.setAccessible(true);
            Object obj = f.get(value);
            if (obj == null || (obj instanceof String && StringUtils.isBlank(obj.toString()))) {
                throw new IllegalArgumentException(f.getName() + "不能为空");
            }
        }
    }

    public static Date getDateHeadOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getDateTailOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getWeekHeadOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getWeekTailOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static Date getMonthHeadOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date getMonthTailOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    public static int calculateDays(Date from, Date to) {
        final long ONE_DAY = 86400000L;
        TimeZone timeZone = TimeZone.getDefault();
        long rawOffset = timeZone.getRawOffset();
        int fromDays = (int) ((from.getTime() + rawOffset) / ONE_DAY);
        int toDays = (int) ((to.getTime() + rawOffset) / ONE_DAY);
        return toDays - fromDays;
    }

    public static List<Map<String, Object>> toMapListExclude(List<?> list, String[] properties) {
        List<Map<String, Object>> back = new ArrayList<>();
        for (Object obj : list) {
            back.add(toMapExclude(obj, properties));
        }
        return back;
    }

    public static Map<String, Object> toMapExclude(Object obj) {
        return MiscUtils.toMapExclude(obj, new String[]{});
    }

    public static Map<String, Object> toMapExclude(Object obj, final String[] properties) {
        return toMap(obj, propertyName -> !ArrayUtils.contains(properties, propertyName));
    }

    public static List<Map<String, Object>> toMapListInclude(List<?> list, final String... properties) {
        List<Map<String, Object>> back = new ArrayList<>();
        for (Object obj : list) {
            back.add(toMapInclude(obj, properties));
        }
        return back;
    }

    public static Map<String, Object> toMapInclude(Object obj, final String... properties) {
        return toMap(obj, propertyName -> ArrayUtils.contains(properties, propertyName));
    }

    public static Map<String, Object> toMap(Object obj, PropertyMatcher propertyMatcher) {
        Map<String, Object> map = new LinkedHashMap<>();
        PropertyDescriptor[] ps = BeanUtils.getPropertyDescriptors(obj.getClass());
        for (PropertyDescriptor p : ps) {
            String propertyName = p.getName();
            if (!"class".equals(propertyName) && propertyMatcher.match(propertyName)) {
                try {

                    Method readMethod = p.getReadMethod();
                    if (readMethod != null) {
                        Object value = readMethod.invoke(obj);
                        map.put(p.getName(), value);
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    LOG.error(null, ex);
                }
            }
        }
        return map;
    }

    /**
     * 导出excel内容，采取的是csv格式的。
     */
    public static void exportExcel(HttpServletRequest request, HttpServletResponse response, Map<String, String> header,
                                   List<Map<String, Object>> dataList) throws IOException {

        StringBuilder sb = new StringBuilder();
        List<String> keyList = new ArrayList<>();
        int index = 0;
        //生成头部
        for (Entry<String, String> entry : header.entrySet()) {
            if (index > 0) {
                sb.append(",");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(value.replace(',', '，'));
            keyList.add(key);
            index++;
        }
        sb.append("\r\n");
        //生成数据
        for (Map<String, Object> map : dataList) {
            index = 0;
            for (String key : keyList) {
                Object value = map.get(key);
                if (index > 0) {
                    sb.append(",");
                }
                if (value == null) {
                } else {
                    sb.append("=\"");
                    sb.append(String.valueOf(value).replace('"', '“'));
                    sb.append("\"");
                }
                index++;
            }
            sb.append("\r\n");
        }
        //CSV格式的，必须是GBK编码的，所以在这里就写成GBK编码了
        byte[] data = sb.toString().getBytes("GBK");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=data.csv");
        response.setContentLength(data.length);
        try (OutputStream os = response.getOutputStream()) {
            os.write(data);
        }
    }

    public static BigInteger getVersionNumber(String version) {
        if (StringUtils.isBlank(version)) {
            return null;
        }
        version = StringUtils.strip(version, "Vv");
        BigInteger bi = BigInteger.ZERO;
        String[] ss = StringUtils.split(version, '.');
        List<String> list = new ArrayList<>(Arrays.asList(ss));
        //版本号最多5段
        if (ss.length > 5) {
            throw new IllegalArgumentException("版本号不能太长");
        } else {
            for (int i = ss.length; i < 5; i++) {
                list.add("0");
            }
        }
        int count = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            String s = list.get(i);
            int number = NumberUtils.toInt(s, 0);
            bi = bi.add(BigInteger.valueOf(number).multiply(BASE.pow(count)));
            count++;
        }
        return bi;
    }

    public static String getToday() {
        return formatDate(new Date(), "yyyy-MM-dd");
    }

    public static String getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return formatDate(cal.getTime(), "yyyy-MM-dd");
    }

    /**
     * 获取参数，如果有多个，则以半角的逗号分隔
     *
     * @param request
     * @param name
     * @return
     */
    public static String getParam(HttpServletRequest request, String name) {
        String[] ss = request.getParameterValues(name);
        if (ArrayUtils.isEmpty(ss)) {
            return null;
        }
        if (ss.length == 1) {
            return ss[0];
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ss.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(ss[i]);
        }
        return sb.toString();
    }


    /**
     * 日期模式常量定义
     */

    public static String getFullUrl(HttpServletRequest request) {
        String query = request.getQueryString();
        if (StringUtils.isBlank(query)) {
            return request.getRequestURI();
        } else {
            return request.getRequestURI() + "?" + query;
        }
    }

    public static String getIp(HttpServletRequest request) {
        try {
            String header = request.getHeader("X-Real-IP");
            if (StringUtils.isNotBlank(header)) {
                return header;
            }
            header = request.getHeader("X-Forwarded-For");
            if (StringUtils.isNotBlank(header)) {
                String[] ss = header.split(",");
                if (ss.length > 0) {
                    return ss[0].trim();
                }
                return header;
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
        return request.getRemoteAddr();
    }

    public static int getCalendarField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    public static List<Map<String, Object>> toMapList(List<?> list, String... properties) {
        List<Map<String, Object>> back = new ArrayList<>();
        for (Object obj : list) {
            back.add(toMap(obj, properties));
        }
        return back;
    }

    public static Map<String, Object> toMap(Object obj, String... properties) {
        return toMapInclude(obj, properties);
    }

    public static boolean isValidAccount(String account) {
        if (StringUtils.isBlank(account)) {
            return false;
        }
        return account.matches(ACCOUNT_REGEX);
    }

    public static boolean isValidCellphoneNum(String cellphoneNum) {
        if (StringUtils.isBlank(cellphoneNum)) {
            return false;
        }
        return cellphoneNum.matches("(1[3456789]\\d{9})");
    }

    public static boolean isValidEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        }
        return email.matches("([\\w\\-.]+@([0-9a-zA-Z_\\-]+\\.)+[a-zA-Z]+)");
    }

    public static <T extends Enum<T>> T getEnumIgnoreCase(String name, Class<T> cls) {
        try {
            T[] ts = cls.getEnumConstants();
            for (T t : ts) {
                if (t.name().equalsIgnoreCase(name)) {
                    return t;
                }
            }
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
        return null;
    }

    public static <T extends Enum<T>> T getEnum(String name, Class<T> cls) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        try {
            return Enum.valueOf(cls, name);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean isEquals(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) {
            return true;
        }
        if (obj1 == null || obj2 == null) {
            return false;
        }
        return obj1.equals(obj2);
    }

    public static Date parseDate(String s, String... patterns) {
        try {
            return DateUtils.parseDate(s, patterns);
        } catch (Exception ex) {
            LOG.error(null, ex);
        }
        return null;
    }

    public static int dateToInt(Date date) {
        String ss = formatDate(date, "yyyyMMdd");
        return NumberUtils.toInt(ss);
    }

    public static long dateToLong(Date date) {
        String ss = formatDate(date, "yyyyMMddHHmmss");
        return NumberUtils.toLong(ss);
    }

    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        if (StringUtils.isBlank(pattern)) {
            pattern = STANDARDPATTERN;
        }
        return new DateTime(date.getTime()).toString(pattern);
    }

    /**
     * 将value值限定的min和max之间的范围 用法参见单元测试
     *
     * @param value 需要判定范围的值
     * @param min 最小值
     * @param max 最大值
     * @return 如果超出范围则返回端点值，否则返回原值
     */
    public static int region(int value, int min, int max) {
        if (value < min) {
            value = min;
        }
        if (value > max) {
            value = max;
        }
        return value;
    }

    /**
     * 判断map是否为null或空Map
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断map是否非空
     */
    public static boolean isNotEmpty(Map map) {
        return map != null && !map.isEmpty();
    }

    /**
     * 取得页数（最小值1，表示共能分1页）
     *
     * @param count 总记录数
     * @param pageSize 每页记录数
     */
    public static int getPageCount(int count, int pageSize) {
        int pageCount = count / pageSize;
        if (count % pageSize > 0) {
            pageCount = pageCount + 1;
        }
        return pageCount;
    }

    /**
     * 分页取List中的记录
     *
     * @return 不会返回null值，全部用空集合代替
     */
    public static <T> List<T> subList(List<T> list, int pageNumber, int pageSize) {
        Validate.isTrue(pageNumber >= 0, "页号必须大于或等于0");
        Validate.isTrue(pageSize > 0, "每页记录数必须大于0");
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        int count = list.size();
        int pageCount = getPageCount(count, pageSize);
        if (pageNumber + 1 > pageCount) { // pageNumber=5则为6页，因为pageNumber从0起
            return Collections.emptyList();  //页号超出时返回空集，不抛异常
        }
        if (count <= pageSize) {
            return new ArrayList<>(list);
        }

        List<T> result = new ArrayList<>(pageSize);
        int startIndex = pageNumber * pageSize;
        int endIndex = startIndex + pageSize - 1;
        if (endIndex >= count) {
            endIndex = count - 1;
        }
        for (int i = startIndex; i <= endIndex; i++) {
            result.add(list.get(i));
        }
        return result;
    }

    /**
     * 取得一个随机的子集合
     *
     * @param mainList 主集合
     * @param count 要取得子集合的大小
     * @return 参数null值也不会报异常，直接返回一个空集
     */
    public static <T> List<T> randomSubList(List<T> mainList, int count) {
        Random random = new Random();
        if (mainList == null || mainList.isEmpty() || count < 1) {
            return Collections.emptyList();
        }

        List<T> cloneList = new ArrayList<>(mainList); //克隆一个，不污染参数的集合
        int length = cloneList.size();
        if (length <= count) {
            Collections.shuffle(cloneList);
            return cloneList;
        }

        List<T> resultList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int index = random.nextInt(length--); //参数为6，则得到0~5之间的随机数
            T o = cloneList.remove(index);
            resultList.add(o);
        }
        return resultList;
    }

    /**
     * 获取当前进程的PID
     */
    public static int getPid() {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String name = runtime.getName(); // format: "pid@hostname"
        try {
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 如果date1在date2前面，则返回正数，否则，返回负数
     * 也就是说，如果date1是2000年，而date2是2010年，则返回10，单位为年
     * 此处返回的是经过毫秒数计算以后，整除的数量，所以是足量，比如：
     * 2013-06-01 23:59:59 和2013-06-02 00:00:01
     * 他之间相差0天，0小时，1分钟，62秒，而不是1天，虽然他们从自然时间上算应该是1天
     */
    public static TimeSpan getSpanOfDate(Date date1, Date date2, TimeUnit unit) {
        if (date1 == null || date2 == null) {
            return null;
        }
        long span = date2.getTime() - date1.getTime();
        TimeSpan timeSpan = new TimeSpan();
        timeSpan.value = unit.convert(span, TimeUnit.MILLISECONDS);
        timeSpan.unit = unit;
        return timeSpan;

    }

    /**
     * 获取发布时间差
     */
    public static String getBetweenTime(Date date) {
        if (date == null) {
            return null;
        }
        if (date.getTime() > System.currentTimeMillis()) {
            return "刚刚";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date now = new Date(System.currentTimeMillis());
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);

        if (day == 0 && hour == 0 && min == 0) {
            return "刚刚";
        } else if (day == 0 && hour == 0) {
            return min + "分钟前";
        } else if (day == 0) {
            return hour + "小时前";
        } else {
            return sdf.format(date);
        }
    }

    /**
     * 是否过了给定的时间点
     *
     * @return null = null
     */
    public static boolean isPass(Date date) {
        if (date == null) {
            return true;
        } else {
            return System.currentTimeMillis() > date.getTime();
        }
    }

    public static boolean toBoolean(String s) {
        return toBoolean(s, false);
    }

    public static boolean toBoolean(String s, boolean defaultValue) {
        if (s != null) {
            String ls = s.toLowerCase();
            return "true".equals(ls) || "yes".equals(ls) || "on".equals(ls) || "y".equals(ls) || "1".equals(ls);
        }
        return defaultValue;
    }

    public static Long getLong(String s, Long defaultValue) {
        if (StringUtils.isNotBlank(s)) {
            return Long.valueOf(s);
        }
        return defaultValue;
    }

    /**
     * 功能：随机排序指定集合取出指定的记录（没出现过的数据优先出现，出现次数少的数据优先出现）
     */
    public static <T> List<Integer> getSortedWeightDataList(List<T> randomSortList, int count,
            Map<Integer, Integer> showData) {
        final int NEW_WEIGHT = 10000;
        final int COUNT_WEIGHT = -100;
        List<WeightData> list = new LinkedList<>();
        Random rnd = new Random();
        for (Object id : randomSortList) {
            WeightData data = new WeightData(Integer.parseInt(id.toString()), rnd.nextInt(NEW_WEIGHT) + NEW_WEIGHT);
            list.add(data);
        }

        for (Entry<Integer, Integer> entry : showData.entrySet()) {
            int id = entry.getKey();
            int theCount = entry.getValue();
            for (WeightData data : list) {
                if (data.getId() == id) {
                    data.setWeight(theCount * COUNT_WEIGHT + rnd.nextInt(NEW_WEIGHT));
                    break;
                }
            }
        }

        Collections.sort(list);

        List<Integer> temp = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            temp.add(list.get(i).getId());
        }
        return temp;
    }

    public static void guardNull(Object obj, String name) {
        if (obj == null) {
            throw new NullPointerException(name + "是null");
        }
    }

    public static class TimeSpan {

        public long value;
        public TimeUnit unit;
    }

    public static class WeightData implements Comparable<WeightData> {

        private int id;
        private int weight;

        public WeightData(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        @Override
        public int compareTo(WeightData another) {
            return another.weight - this.weight;
        }
    }

    public interface PropertyMatcher {
        boolean match(String propertyName);
    }

    public static int safeAbs(int i) {
        if (i == Integer.MIN_VALUE) {
            return Integer.MAX_VALUE;
        } else {
            return Math.abs(i);
        }
    }

    public static long safeAbs(long i) {
        if (i == Long.MIN_VALUE) {
            return Long.MAX_VALUE;
        } else {
            return Math.abs(i);
        }
    }
}
