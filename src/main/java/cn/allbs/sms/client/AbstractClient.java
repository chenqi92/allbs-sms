package cn.allbs.sms.client;

import cn.allbs.sms.exception.SmsException;
import cn.allbs.sms.properties.SmsDetailProperties;
import cn.allbs.sms.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * 类 AbstractClient
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/8 17:36
 */
@Slf4j
public abstract class AbstractClient implements IClient {

    protected Map<String, SmsDetailProperties> smsTemplateMap;

    protected String appKey;

    protected String appSecret;

    protected String region;

    protected String endPoint;

    protected Long timeOut;

    public AbstractClient(SmsProperties smsProperties) {
        if (!Optional.ofNullable(smsProperties).isPresent() || smsProperties.getTemplates().isEmpty()) {
            throw new SmsException("短信通道信息未配置,短信服务不可用!");
        }
        this.smsTemplateMap = smsProperties.getTemplates();
        this.appKey = smsProperties.getAppKey();
        this.appSecret = smsProperties.getAppSecret();
        this.timeOut = smsProperties.getTimeout();
        this.region = smsProperties.getRegion();
        this.endPoint = smsProperties.getEndPoint();
    }

    @Override
    public String sendCode(String code, String... phoneNumbers) throws Exception {
        return null;
    }

    @Override
    public String sendCode(String templateId, String code, String... phoneNumbers) throws Exception {
        return null;
    }

    @Override
    public String send() throws Exception {
        return null;
    }

    @Override
    public String send(String templateId, Map<String, String> params, String... phoneNumbers) throws Exception {
        return null;
    }

    /**
     * @param smsDetailProperties 当前模版属性值
     */
    public void propertiesChange(SmsDetailProperties smsDetailProperties) {
        this.appKey = Optional.ofNullable(smsDetailProperties).map(SmsDetailProperties::getAppKey).orElse(this.appKey);
        this.appSecret = Optional.ofNullable(smsDetailProperties).map(SmsDetailProperties::getAppSecret).orElse(this.appSecret);
        this.region = Optional.ofNullable(smsDetailProperties).map(SmsDetailProperties::getRegion).orElse(this.region);
        this.endPoint = Optional.ofNullable(smsDetailProperties).map(SmsDetailProperties::getEndPoint).orElse(this.endPoint);
    }
}
