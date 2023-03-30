package cn.allbs.sms.client;

import cn.allbs.sms.properties.SmsClientProperties;
import cn.allbs.sms.properties.SmsDetailProperties;
import cn.allbs.sms.util.Utils;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 类 AliyunClient
 * </p>
 *
 * @author ChenQi
 * @since 2022/11/8 17:43
 */
@Slf4j
public class AliYunClient extends AbstractClient {

    public AliYunClient(SmsClientProperties smsClientProperties) {
        super(smsClientProperties.getAli());
    }

    @Override
    public String sendCode(String code, String... phoneNumbers) throws Exception {
        String res = "";
        // 获取第一个模版
        String templateId = this.smsTemplateMap.keySet().iterator().next();
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        Client client = buildClient(templateId);

        SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(Utils.join(phoneNumbers, ",")).setSignName(sms.getSignName()).setTemplateCode(templateId).setTemplateParam(sms.getTemplateParam().toString()).setSmsUpExtendCode(sms.getRemarks()).setOutId(sms.getExpandContent());
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            res = response.toString();
        } catch (TeaException error) {
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        }
        return res;
    }

    @Override
    public String sendCode(String templateId, String code, String... phoneNumbers) throws Exception {
        String res = "";
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        Client client = buildClient(templateId);
        Map<String, String> params = new HashMap<>(1);
        params.put("code", code);
        SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(Utils.join(phoneNumbers, ",")).setSignName(sms.getSignName()).setTemplateCode(templateId).setTemplateParam(params.toString()).setSmsUpExtendCode(sms.getRemarks()).setOutId(sms.getExpandContent());
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            res = response.toString();
        } catch (TeaException error) {
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        }
        return res;
    }

    @Override
    public String send() throws Exception {
        String res = "";
        // 获取第一个模版
        String templateId = this.smsTemplateMap.keySet().iterator().next();
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        Client client = buildClient(templateId);

        SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(Utils.join(sms.getPhoneNumbers(), ",")).setSignName(sms.getSignName()).setTemplateCode(templateId).setTemplateParam(sms.getTemplateParam().toString()).setSmsUpExtendCode(sms.getRemarks()).setOutId(sms.getExpandContent());
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            res = response.toString();
        } catch (TeaException error) {
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        }
        return res;
    }

    @Override
    public String send(String templateId, Map<String, String> params, String... phoneNumbers) throws Exception {
        String res = "";
        // 获取第一个模版
        SmsDetailProperties sms = this.smsTemplateMap.get(templateId);
        Client client = buildClient(templateId);

        SendSmsRequest sendSmsRequest = new SendSmsRequest().setPhoneNumbers(Utils.join(phoneNumbers, ",")).setSignName(sms.getSignName()).setTemplateCode(templateId).setTemplateParam(params.toString()).setSmsUpExtendCode(sms.getRemarks()).setOutId(sms.getExpandContent());
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            res = response.toString();
        } catch (TeaException error) {
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            // 如有需要，请打印 error
            res = error.message;
            log.warn("allbs-sms告警提示{}", res);
        }
        return res;
    }

    private Client buildClient(String templateId) throws Exception {
        SmsDetailProperties sms = Optional.ofNullable(this.smsTemplateMap).map(a -> a.get(templateId)).orElse(null);
        super.propertiesChange(sms);
        Config config = new Config().setAccessKeyId(this.appKey).setAccessKeySecret(this.appSecret);
        config.setEndpoint(this.endPoint);
        config.setRegionId(this.region);
        return new Client(config);
    }
}
