package cn.allbs.sms.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 类 SmsProperties
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/4 17:52
 */
@Getter
@Setter
public class SmsProperties {

    /**
     * 模版信息
     */
    private Map<String, SmsDetailProperties> templates;

    /**
     * 是否启用 默认为：true
     */
    private boolean enable = true;

    /**
     * APP_Key
     */
    private String appKey;

    /**
     * APP_Secret
     */
    private String appSecret;

    /**
     * 过期时间，单位秒
     */
    private Long timeout = 60L;

    /**
     * 地域信息
     */
    private String region;

    /**
     * 各服务商短信发送地址
     */
    private String endPoint;
}
