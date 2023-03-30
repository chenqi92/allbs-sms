package cn.allbs.sms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 类 SmsClientProperties
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/8 16:46
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SmsClientProperties {

    /**
     * 阿里云客户端
     */
    private SmsProperties ali;

    /**
     * 腾讯云客户端
     */
    private SmsProperties tx;

    /**
     * 华为云客户端
     */
    private SmsProperties huawei;

}
