## 使用
### 添加依赖

```xml
<dependency>
  <groupId>cn.allbs</groupId>
  <artifactId>allbs-sms</artifactId>
  <version>2.0.1</version>
</dependency>
<!-- 使用阿里云短信发送服务映引入 -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>dysmsapi20170525</artifactId>
    <version>${alibabacloud.version}</version>
</dependency>
<!-- 使用腾讯云短信发送服务映引入 -->
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-sdk-java</artifactId>
    <version>${tencent.version}</version>
</dependency>
```

### 添加配置

```yaml
sms:
  # 华为云 如果不使用华为云的短信服务可以不用配置
  huawei:
    enable: true
    end-point: https://rtcsms.cn-north-1.myhuaweicloud.com:10743/sms/batchSendSms/v1
    app-key: xxxxxxxxxxxxxxxxxx
    app-secret: xxxxxxxxxxxxxx
    templates:
      ff23bc1c9ed64a3b94044369c4de4933:
        sign-name: 签名名称
        sdk-app-id: 8820112313636
  # 阿里云 如果不使用阿里云的短信服务可以不用配置
  ali:
    enable: true
    end-point: xxxxxxxxxx
    app-key: xxxx
    app-secret: xxxxx
    templates:
      xxxxxxxxxxxxxxxxxxxxxxxx:
        sign-name: 签名名称
  # 腾讯云 如果不使用腾讯云的短信服务可以不用配置
  tx:
    enable: true
    end-point: xxxxx
    app-key: xxxxxxxxxx
    app-secret: xxxxxxxxxxx
    templates:
      xxxxxxxxxxxxxxxx:
        sign-name: xxxxxxxxxxxx
      xxxxxxxxxxxxxxxx-2:
        sign-name: xxxxxxxxxxxxxx
```

### 引入发送端

```java
/**
 * 华为云客户端 不使用不要引入
 */
@Resource
private HuaWeiYunClient huaWeiYunClient;

/**
 * 阿里云客户端 不使用不要引入
 */
@Resource
private AliYunClient aliYunClient;

/**
 * 腾讯云客户端 不使用不要引入
 */
@Resource
private TencentCloudClient tencentCloudClient;
```

### 推送短信

```java
// 短信推送模板ID
String templateId = "ff23bc1c9ed64a3b94044369c4de4933";
Map<String, String> params = new HashMap<>(2);
params.put("1", "Allbs");
params.put("2", "模拟测试");
huaWeiYunClient.send(templateId, params, "18066081000", "18066081001");
```

