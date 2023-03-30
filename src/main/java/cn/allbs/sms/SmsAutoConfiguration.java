package cn.allbs.sms;

import cn.allbs.sms.client.AliYunClient;
import cn.allbs.sms.client.HuaWeiYunClient;
import cn.allbs.sms.client.TencentCloudClient;
import cn.allbs.sms.properties.SmsClientProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类 SmsAutoConfiguration
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/4 17:50
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SmsClientProperties.class)
public class SmsAutoConfiguration {

    private final SmsClientProperties smsClientProperties;

    /**
     * 阿里云客户端
     *
     * @return 客户端
     */
    @Bean
    @ConditionalOnMissingBean(AliYunClient.class)
    @ConditionalOnProperty(prefix = "sms.ali", name = "enable", havingValue = "true")
    public AliYunClient aliYunClient() {
        return new AliYunClient(smsClientProperties);
    }

    /**
     * 腾讯云客户端
     *
     * @return 客户端
     */
    @Bean
    @ConditionalOnMissingBean(TencentCloudClient.class)
    @ConditionalOnProperty(prefix = "sms.tx", name = "enable", havingValue = "true")
    public TencentCloudClient tencentCloudClient() {
        return new TencentCloudClient(smsClientProperties);
    }

    /**
     * 华为云客户端
     *
     * @return 客户端
     */
    @Bean
    @ConditionalOnMissingBean(HuaWeiYunClient.class)
    @ConditionalOnProperty(prefix = "sms.huawei", name = "enable", havingValue = "true")
    public HuaWeiYunClient huaWeiYunClient() {
        return new HuaWeiYunClient(smsClientProperties);
    }
}
