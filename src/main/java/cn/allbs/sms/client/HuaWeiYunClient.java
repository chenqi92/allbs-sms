package cn.allbs.sms.client;

import cn.allbs.sms.properties.SmsClientProperties;
import cn.allbs.sms.properties.SmsDetailProperties;
import cn.allbs.sms.util.Utils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cn.allbs.sms.constant.CommonConstant.CODE;
import static cn.allbs.sms.constant.CommonConstant.MSG_OK;

/**
 * 类 HuaWeiYunClient
 * </p>
 * 华为云短信发送端
 *
 * @author ChenQi
 * @since 2022/11/8 17:44
 */
@Slf4j
public class HuaWeiYunClient extends AbstractClient {

    /**
     * 无需修改,用于格式化鉴权头域,给"X-WSSE"参数赋值
     */
    private static final String WSSE_HEADER_FORMAT = "UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"";
    /**
     * 无需修改,用于格式化鉴权头域,给"Authorization"参数赋值
     */
    private static final String AUTH_HEADER_VALUE = "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"";

    public HuaWeiYunClient(SmsClientProperties smsClientProperties) {
        super(smsClientProperties.getHuawei());
    }

    @Override
    public String sendCode(String code, String... phoneNumbers) throws Exception {
        String templateId = this.smsTemplateMap.keySet().iterator().next();
        SmsDetailProperties sms = Optional.ofNullable(this.smsTemplateMap).map(a -> a.get(templateId)).orElse(null);
        super.propertiesChange(sms);
        Gson gson = new Gson();
        assert sms != null;
        String templateParas = gson.toJson(sms.getTemplateParam().values());
        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = "";
        //请求Body,不携带签名名称时,signature请填null
        String body = buildRequestBody(sms.getSdkAppId(), Utils.join(phoneNumbers, ","), templateId, templateParas, statusCallBack, sms.getSignName());
        if (null == body || body.isEmpty()) {
            log.error("allbs-sms 提示: body is null.");
            return null;
        }

        /*
          选填,使用无变量模板时请赋空值 String templateParas = "";
          单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
          双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
          模板中的每个变量都必须赋值，且取值不能为空
          查看更多模板和变量规范:产品介绍>模板和变量规范
         */

        //请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader(appKey, appSecret);
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            log.error("allbs-sms 提示: wsse header is null.");
            return null;
        }
        //如果JDK版本是1.8,可使用如下代码
        //为防止因HTTPS证书认证失败造成API调用失败,需要先忽略证书信任问题
        CloseableHttpClient client = null;
        try {
            client = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                            (x509CertChain, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            //请求方法POST
            assert client != null;
            response = client.execute(RequestBuilder.create("POST")
                    .setUri(this.endPoint)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                    .addHeader("X-WSSE", wsseHeader)
                    .setEntity(new StringEntity(body)).build());
            //打印响应头域信息
            log.info("allbs-sms 提示:  response is" + response.toString());
            //打印响应消息实体
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            if (BeanUtil.isNotEmpty(entity)) {
                Map<String, Object> resultMap = gson.fromJson(result, Map.class);
                log.info("allbs-sms 提示: the message is to be sent " + resultMap);
                if (MSG_OK.equals(resultMap.get(CODE))) {
                    log.info("allbs-sms 提示: 短信发送成功,message content is" + result);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("allbs-sms 提示: 短信API调用失败！");
        }
        return null;
    }

    @Override
    public String sendCode(String templateId, String code, String... phoneNumbers) throws Exception {
        SmsDetailProperties sms = Optional.ofNullable(this.smsTemplateMap).map(a -> a.get(templateId)).orElse(null);
        super.propertiesChange(sms);
        Gson gson = new Gson();
        assert sms != null;
        String templateParas = gson.toJson(sms.getTemplateParam().values());
        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = "";
        //请求Body,不携带签名名称时,signature请填null
        String body = buildRequestBody(sms.getSdkAppId(), Utils.join(phoneNumbers, ","), templateId, templateParas, statusCallBack, sms.getSignName());
        if (null == body || body.isEmpty()) {
            log.error("allbs-sms 提示: body is null.");
            return null;
        }

        /*
          选填,使用无变量模板时请赋空值 String templateParas = "";
          单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
          双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
          模板中的每个变量都必须赋值，且取值不能为空
          查看更多模板和变量规范:产品介绍>模板和变量规范
         */

        //请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader(appKey, appSecret);
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            log.error("allbs-sms 提示: wsse header is null.");
            return null;
        }
        //如果JDK版本是1.8,可使用如下代码
        //为防止因HTTPS证书认证失败造成API调用失败,需要先忽略证书信任问题
        CloseableHttpClient client = null;
        try {
            client = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                            (x509CertChain, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            //请求方法POST
            assert client != null;
            response = client.execute(RequestBuilder.create("POST")
                    .setUri(this.endPoint)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                    .addHeader("X-WSSE", wsseHeader)
                    .setEntity(new StringEntity(body)).build());
            //打印响应头域信息
            log.info("allbs-sms 提示:  response is" + response.toString());
            //打印响应消息实体
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            if (BeanUtil.isNotEmpty(entity)) {
                Map<String, Object> resultMap = gson.fromJson(result, Map.class);
                log.info("allbs-sms 提示: the message is to be sent " + resultMap);
                if (MSG_OK.equals(resultMap.get(CODE))) {
                    log.info("allbs-sms 提示: 短信发送成功,message content is" + result);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("allbs-sms 提示: 短信API调用失败！");
        }
        return null;
    }

    @Override
    public String send() throws Exception {
        String templateId = this.smsTemplateMap.keySet().iterator().next();
        SmsDetailProperties sms = Optional.ofNullable(this.smsTemplateMap).map(a -> a.get(templateId)).orElse(null);
        super.propertiesChange(sms);
        Gson gson = new Gson();
        assert sms != null;
        String templateParas = gson.toJson(sms.getTemplateParam().values());
        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = "";
        //请求Body,不携带签名名称时,signature请填null
        String body = buildRequestBody(sms.getSdkAppId(), Utils.join(sms.getPhoneNumbers(), ","), templateId, templateParas, statusCallBack, sms.getSignName());
        if (null == body || body.isEmpty()) {
            log.error("allbs-sms 提示: body is null.");
            return null;
        }

        /*
          选填,使用无变量模板时请赋空值 String templateParas = "";
          单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
          双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
          模板中的每个变量都必须赋值，且取值不能为空
          查看更多模板和变量规范:产品介绍>模板和变量规范
         */

        //请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader(appKey, appSecret);
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            log.error("allbs-sms 提示: wsse header is null.");
            return null;
        }
        //如果JDK版本是1.8,可使用如下代码
        //为防止因HTTPS证书认证失败造成API调用失败,需要先忽略证书信任问题
        CloseableHttpClient client = null;
        try {
            client = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                            (x509CertChain, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            //请求方法POST
            assert client != null;
            response = client.execute(RequestBuilder.create("POST")
                    .setUri(this.endPoint)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                    .addHeader("X-WSSE", wsseHeader)
                    .setEntity(new StringEntity(body)).build());
            //打印响应头域信息
            log.info("allbs-sms 提示:  response is" + response.toString());
            //打印响应消息实体
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            if (BeanUtil.isNotEmpty(entity)) {
                Map<String, Object> resultMap = gson.fromJson(result, Map.class);
                log.info("allbs-sms 提示: the message is to be sent " + resultMap);
                if (MSG_OK.equals(resultMap.get(CODE))) {
                    log.info("allbs-sms 提示: 短信发送成功,message content is" + result);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("allbs-sms 提示: 短信API调用失败！");
        }
        return null;
    }

    @Override
    public String send(String templateId, Map<String, String> params, String... phoneNumbers) throws Exception {
        SmsDetailProperties sms = Optional.ofNullable(this.smsTemplateMap).map(a -> a.get(templateId)).orElse(null);
        super.propertiesChange(sms);
        Gson gson = new Gson();
        assert sms != null;
        String templateParas = gson.toJson(params.values());
        //选填,短信状态报告接收地址,推荐使用域名,为空或者不填表示不接收状态报告
        String statusCallBack = "";
        //请求Body,不携带签名名称时,signature请填null
        String body = buildRequestBody(sms.getSdkAppId(), Utils.join(phoneNumbers, ","), templateId, templateParas, statusCallBack, sms.getSignName());
        if (null == body || body.isEmpty()) {
            log.error("allbs-sms 提示: body is null.");
            return null;
        }

        /*
          选填,使用无变量模板时请赋空值 String templateParas = "";
          单变量模板示例:模板内容为"您的验证码是${1}"时,templateParas可填写为"[\"369751\"]"
          双变量模板示例:模板内容为"您有${1}件快递请到${2}领取"时,templateParas可填写为"[\"3\",\"人民公园正门\"]"
          模板中的每个变量都必须赋值，且取值不能为空
          查看更多模板和变量规范:产品介绍>模板和变量规范
         */

        //请求Headers中的X-WSSE参数值
        String wsseHeader = buildWsseHeader(appKey, appSecret);
        if (null == wsseHeader || wsseHeader.isEmpty()) {
            log.error("allbs-sms 提示: wsse header is null.");
            return null;
        }
        //如果JDK版本是1.8,可使用如下代码
        //为防止因HTTPS证书认证失败造成API调用失败,需要先忽略证书信任问题
        CloseableHttpClient client = null;
        try {
            client = HttpClients.custom()
                    .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null,
                            (x509CertChain, authType) -> true).build())
                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        HttpResponse response = null;
        try {
            //请求方法POST
            assert client != null;
            response = client.execute(RequestBuilder.create("POST")
                    .setUri(this.endPoint)
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                    .addHeader(HttpHeaders.AUTHORIZATION, AUTH_HEADER_VALUE)
                    .addHeader("X-WSSE", wsseHeader)
                    .setEntity(new StringEntity(body)).build());
            //打印响应头域信息
            log.info("allbs-sms 提示:  response is" + response.toString());
            //打印响应消息实体
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            if (BeanUtil.isNotEmpty(entity)) {
                Map<String, Object> resultMap = gson.fromJson(result, Map.class);
                log.info("allbs-sms 提示: the message is to be sent " + resultMap);
                if (MSG_OK.equals(resultMap.get(CODE))) {
                    log.info("allbs-sms 提示: 短信发送成功,message content is" + result);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            log.error("allbs-sms 提示: 短信API调用失败！");
        }
        return null;
    }

    /**
     * 构造请求Body体
     *
     * @param sender            发送方
     * @param receiver          接收方
     * @param templateId        模板id
     * @param templateParas     模板参数
     * @param statusCallbackUrl 回调url
     * @param signature         | 签名名称,使用国内短信通用模板时填写
     * @return 请求连接
     */
    private String buildRequestBody(String sender, String receiver, String templateId, String templateParas,
                                    String statusCallbackUrl, String signature) {
        if (null == sender || null == receiver || null == templateId || sender.isEmpty() || receiver.isEmpty()
                || templateId.isEmpty()) {
            log.error("allbs-sms 提示: buildRequestBody sender, receiver or templateId is null.");
            return null;
        }
        List<NameValuePair> keyValues = new ArrayList<>();

        keyValues.add(new BasicNameValuePair("from", sender));
        keyValues.add(new BasicNameValuePair("to", receiver));
        keyValues.add(new BasicNameValuePair("templateId", templateId));
        if (null != templateParas && !templateParas.isEmpty()) {
            keyValues.add(new BasicNameValuePair("templateParas", templateParas));
        }
        if (null != statusCallbackUrl && !statusCallbackUrl.isEmpty()) {
            keyValues.add(new BasicNameValuePair("statusCallback", statusCallbackUrl));
        }
        if (null != signature && !signature.isEmpty()) {
            keyValues.add(new BasicNameValuePair("signature", signature));
        }

        return URLEncodedUtils.format(keyValues, StandardCharsets.UTF_8);
    }

    /**
     * 构造X-WSSE参数值
     *
     * @param appKey    appKey
     * @param appSecret appSecret
     * @return 请求连接
     */
    private String buildWsseHeader(String appKey, String appSecret) {
        if (null == appKey || null == appSecret || appKey.isEmpty() || appSecret.isEmpty()) {
            log.error("allbs-sms 提示: appKey or appSecret is null in method buildWsseHeader");
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DatePattern.UTC_PATTERN);
        //Created
        String time = sdf.format(new Date());
        //Nonce
        String nonce = UUID.randomUUID().toString().replace("-", "");

        byte[] passwordDigest = DigestUtils.sha256(nonce + time + appSecret);
        String hexDigest = Hex.encodeHexString(passwordDigest);

        //如果JDK版本是1.8,请加载原生Base64类,并使用如下代码
        //PasswordDigest
        String passwordDigestBase64Str = Base64.getEncoder().encodeToString(hexDigest.getBytes());
        return String.format(WSSE_HEADER_FORMAT, appKey, passwordDigestBase64Str, nonce, time);
    }


}
