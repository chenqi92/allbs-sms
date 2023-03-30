package cn.allbs.sms.properties;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 类 SmsTemplate
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/4 17:55
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsDetailProperties {

    /**
     * APP_Key
     */
    private String appKey;

    /**
     * APP_Secret
     */
    private String appSecret;

    /**
     * 签名名称
     */
    private String signName;

    /**
     * 过期时间，单位秒
     */
    private Long timeout = 60L;

    /**
     * 地域信息
     */
    private String region;

    /**
     * 模版code
     */
    private String templateCode;

    /**
     * 模版参数
     */
    private Map<String, String> templateParam;

    /**
     * 发送的手机号
     */
    private List<String> phoneNumbers;

    /**
     * 端点
     */
    private String endPoint;

    /**
     * 短信应用id
     */
    private String sdkAppId;

    /**
     * 备注内容
     */
    private String remarks = "";

    /**
     * 扩展内容
     */
    private String expandContent;
}
