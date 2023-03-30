package cn.allbs.sms.util;

import cn.hutool.core.convert.ConverterRegistry;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 类 Utils
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/9 9:44
 */
@UtilityClass
public class Utils {

    private static final String SUCCESS_CODE = "OK";

    /**
     * 宽松校验即可.
     */
    private static final String PHONE_NUMBER_REGEX = "\\d{5,}";

    /**
     * 生成随机验证码.
     *
     * @return 随机数
     */
    public int randomCode() {
        return 100_000 + ThreadLocalRandom.current().nextInt(1_000_000 - 100_000);
    }

    /**
     * Map 转 json 字符串的简单实现.
     *
     * @param map the map
     * @return the json string
     */
    public String toJsonStr(final Map<String, String> map) {
        if (null == map || map.isEmpty()) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            sb.append('"').append(entry.getKey().replace("\"", "\\\"")).append('"').append(':').append('"').append(entry.getValue().replace("\"", "\\\"")).append('"').append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append('}');
        return sb.toString();
    }

    /**
     * 校验手机号码（中国）.
     *
     * @param phoneNumbers the phone numbers
     */
    public void checkPhoneNumber(final String... phoneNumbers) {

        if (phoneNumbers.length == 0) {
            throw new IllegalArgumentException("Invalid phone size");
        }

        for (String phoneNumber : phoneNumbers) {
            if (!phoneNumber.matches(PHONE_NUMBER_REGEX)) {
                throw new IllegalArgumentException("Invalid phone number " + phoneNumber);
            }
        }
    }

    /**
     * 校验字符串不为空.
     *
     * @param str     the str
     * @param message the message
     */
    public void checkNotEmpty(final String str, final String message) {
        if (null == str || str.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 校验集合不为空.
     *
     * @param coll    the Collection
     * @param message the message
     */
    public void checkNotEmpty(final Collection coll, final String message) {
        if (null == coll || coll.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static <T> String join(T[] array, CharSequence conjunction) {
        return join(array, conjunction, null, null);
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>       被处理的集合
     * @param array     数组
     * @param delimiter 分隔符
     * @param prefix    每个元素添加的前缀，null表示不添加
     * @param suffix    每个元素添加的后缀，null表示不添加
     * @return 连接后的字符串
     * @since 4.0.10
     */
    public static <T> String join(T[] array, CharSequence delimiter, String prefix, String suffix) {
        if (null == array) {
            return null;
        }

        return StrJoiner.of(delimiter, prefix, suffix)
                // 每个元素都添加前后缀
                .setWrapElement(true)
                .append(array)
                .toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param <T>         被处理的集合
     * @param array       数组
     * @param conjunction 分隔符
     * @param editor      每个元素的编辑器，null表示不编辑
     * @return 连接后的字符串
     * @since 5.3.3
     */
    public static <T> String join(T[] array, CharSequence conjunction, Editor<T> editor) {
        return StrJoiner.of(conjunction).append(array, (t) -> String.valueOf(editor.edit(t))).toString();
    }

    /**
     * 以 conjunction 为分隔符将数组转换为字符串
     *
     * @param array       数组
     * @param conjunction 分隔符
     * @return 连接后的字符串
     */
    public static String join(Object array, CharSequence conjunction) {
        if (null == array) {
            return null;
        }
        if (!isArray(array)) {
            throw new IllegalArgumentException(StrUtil.format("[{}] is not a Array!", array.getClass()));
        }

        return StrJoiner.of(conjunction).append(array).toString();
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        return null != obj && obj.getClass().isArray();
    }

    /**
     * 转换为int<br>
     * 如果给定的值为空，或者转换失败，返回默认值<br>
     * 转换失败不会报错
     *
     * @param value        被转换的值
     * @param defaultValue 转换错误时的默认值
     * @return 结果
     */
    public static Integer toInt(Object value, Integer defaultValue) {
        return convertQuietly(Integer.class, value, defaultValue);
    }

    /**
     * 转换值为指定类型，不抛异常转换<br>
     * 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param type         目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @return 转换后的值
     * @since 4.5.10
     */
    public static <T> T convertQuietly(Type type, Object value, T defaultValue) {
        return convertWithCheck(type, value, defaultValue, true);
    }

    /**
     * 转换值为指定类型，可选是否不抛异常转换<br>
     * 当转换失败时返回默认值
     *
     * @param <T>          目标类型
     * @param type         目标类型
     * @param value        值
     * @param defaultValue 默认值
     * @param quietly      是否静默转换，true不抛异常
     * @return 转换后的值
     * @since 5.3.2
     */
    public static <T> T convertWithCheck(Type type, Object value, T defaultValue, boolean quietly) {
        final ConverterRegistry registry = ConverterRegistry.getInstance();
        try {
            return registry.convert(type, value, defaultValue);
        } catch (Exception e) {
            if (quietly) {
                return defaultValue;
            }
            throw e;
        }
    }

}
