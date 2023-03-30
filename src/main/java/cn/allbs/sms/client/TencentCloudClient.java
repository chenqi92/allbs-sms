package cn.allbs.sms.client;

import cn.allbs.sms.properties.SmsClientProperties;
import cn.allbs.sms.properties.SmsDetailProperties;
import cn.allbs.sms.util.Utils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;

import java.util.Map;
import java.util.Optional;

/**
 * 类 TencentCloudClient
 * </p>
 * 腾讯云短信发送端
 *
 * @author ChenQi
 * @since 2022/11/8 17:44
 */
public class TencentCloudClient extends AbstractClient {
    public TencentCloudClient(SmsClientProperties smsClientProperties) {
        super(smsClientProperties.getTx());
    }

    @Override
    public String sendCode(String code, String... phoneNumbers) throws TencentCloudSDKException {
        // 获取第一个模版
        String templateId = this.smsTemplateMap.keySet().iterator().next();
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        SmsClient client = buildClient(templateId);
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(sms.getSdkAppId());
        req.setSignName(sms.getSignName());
        req.setTemplateId(templateId);
        req.setTemplateParamSet(new String[]{code});
        req.setPhoneNumberSet(phoneNumbers);
        req.setSessionContext(sms.getRemarks());
        SendSmsResponse res = client.SendSms(req);
        return SendSmsResponse.toJsonString(res);
    }

    @Override
    public String sendCode(String templateId, String code, String... phoneNumbers) throws TencentCloudSDKException {
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        SmsClient client = buildClient(templateId);
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(sms.getSdkAppId());
        req.setSignName(sms.getSignName());
        req.setTemplateId(templateId);
        req.setTemplateParamSet(new String[]{code});
        req.setPhoneNumberSet(phoneNumbers);
        req.setSessionContext(sms.getRemarks());
        SendSmsResponse res = client.SendSms(req);
        return SendSmsResponse.toJsonString(res);
    }

    @Override
    public String send() throws TencentCloudSDKException {
        // 获取第一个模版
        String templateId = this.smsTemplateMap.keySet().iterator().next();
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        SmsClient client = buildClient(templateId);
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(sms.getSdkAppId());
        req.setSignName(sms.getSignName());
        req.setTemplateId(templateId);
        req.setTemplateParamSet(sms.getTemplateParam().values().toArray(new String[0]));
        req.setPhoneNumberSet(sms.getPhoneNumbers().toArray(new String[0]));
        req.setSessionContext(sms.getRemarks());
        SendSmsResponse res = client.SendSms(req);
        return SendSmsResponse.toJsonString(res);
    }

    @Override
    public String send(String templateId, Map<String, String> params, String... phoneNumbers) throws TencentCloudSDKException {
        return assembleSend(templateId, params.values().toArray(new String[0]), phoneNumbers);
    }

    private String assembleSend(String templateId, String[] params, String... phoneNumbers) throws TencentCloudSDKException {
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        SmsClient client = buildClient(templateId);
        SendSmsRequest req = new SendSmsRequest();
        req.setSmsSdkAppId(sms.getSdkAppId());
        req.setSignName(sms.getSignName());
        req.setTemplateId(templateId);
        req.setTemplateParamSet(params);
        req.setPhoneNumberSet(phoneNumbers);
        req.setSessionContext(sms.getRemarks());
        SendSmsResponse res = client.SendSms(req);
        return SendSmsResponse.toJsonString(res);
    }

    /**
     * 构建短信发送client
     *
     * @param templateId 模版id
     * @return client
     */
    private SmsClient buildClient(String templateId) {
        SmsDetailProperties sms = Optional.ofNullable(this.smsTemplateMap).map(a -> a.get(templateId)).orElse(null);
        super.propertiesChange(sms);
        Credential cred = new Credential(this.appKey, this.appSecret);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod(HttpProfile.REQ_POST);
        httpProfile.setConnTimeout(Utils.toInt(this.timeOut, HttpProfile.TM_MINUTE));
        httpProfile.setEndpoint(this.endPoint);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setSignMethod(ClientProfile.SIGN_SHA256);
        clientProfile.setHttpProfile(httpProfile);
        return new SmsClient(cred, this.region, clientProfile);
    }
}
