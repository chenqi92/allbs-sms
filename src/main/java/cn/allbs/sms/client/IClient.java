package cn.allbs.sms.client;

import java.util.Map;

/**
 * 接口 IClient
 * </p>
 * 短信客户端
 *
 * @author ChenQi
 * @since 2022/11/7 15:55
 */
public interface IClient {

    /**
     * 发送短信验证码。默认使用第一个模版
     *
     * @param code         验证码
     * @param phoneNumbers 手机号
     * @return 发送状态
     */
    String sendCode(final String code, final String... phoneNumbers) throws Exception;

    /**
     * 发送短信验证码。默认使用第一个模版
     *
     * @param templateId   模版key
     * @param code         验证码
     * @param phoneNumbers 手机号
     * @return 发送状态
     */
    String sendCode(final String templateId, final String code, final String... phoneNumbers) throws Exception;

    /**
     * 发送短信，使用配置好的默认短信模版和默认的手机号
     *
     * @return 发送状态
     */
    String send() throws Exception;

    /**
     * 发送短信 指定模版id、参数、手机号
     *
     * @param templateId   模版id
     * @param params       参数
     * @param phoneNumbers 手机号
     * @return 发送状态
     */
    String send(final String templateId, Map<String, String> params, final String... phoneNumbers) throws Exception;

}
