package org.csu.mypetstore.domain;
import org.springframework.context.annotation.Configuration;
import java.io.FileWriter;
import java.io.IOException;
@Configuration
public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102300746182";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCOr6Hy4wcWd1+8n5OASD0wo3V3WH9QRXyJ45o2LI8XLQUKRi4XkvRXemNAecbeViTDV1uQ70T7w9w7v4p8axoIrmRjNro894i/ewGaxXxXgOmM1VhWSeZAhNOVjB9DB25rA3oWxTYnn89M+AysB9aoktsTmKcV1mIsEGD6/76HHORmjkZRcD9gzPyVC7DfX5Jaqx5owGLi5c2p+nDA7riHYgLrqzO+07KBhVopy1jjBUhSshOjS9ujf6pVdBaAD7Vqxlk/QWlIxHw+xGKe3cJj6MrQwT12yYyzxlsC9aPqYQaL65EE3IgdTb2S7pdGqm+kRFDBv3ihu446Pmgl6t4PAgMBAAECggEAedgJF3w+5TRzEIPEVVtaxVoQSSXNMiLkQmuVmIVce5H6J9feLEtVVA4zCy/yXpJ5OkYy7wUjh0qtmmfqPmzHAnrJRt/JEZ7fq4PCzwOtNpvi1Fyq3qSkdpv2glHxsC98Xu3NJ/yoThXt0/BOAel/qNwfMBC/Paqgkrg52JzjekQvaQrGDW+SR1S3H1BmLG+oolpYSH5H2vUBLASZ4gaPNb3f8CjQ4aUJWYRppzjwJ7eMmV3KAd42dU92mkL+RHYb004FYDmc6o+bo5LyE74qo+rVODfmQdkUr4hIhcVqKmA7y8HMLe3jgtH+H1xhdvVRJfh2vNb/Sua1+71tf1doAQKBgQDAyeArHEFanNPjGGg4Hs+/C0H2FUq1fRAE9tx8YmBV8iZ4IFeU0uQUm9FWaa8gxtSv4K1OjPaYNiXnvLj99S6yuqgNd8WpSMTclrGM0TnXLalfmJgpcZif8NHpHABpKzkyy0I0uQE+J0Ey7TO6ew151j88KG1zDj23WHYFVM5LDwKBgQC9eEtzTyPOgKSXML4pMOGzpXfuCRr09BzCYeLrD+4Ok+2yb4GnCNGmbjZssClJUzYxbSisIYxkZsE25+Dgs3q1Px97c8I+tY/e84VCkcnD75zb3OyTTs4NT1jTG3Z7r3aymPw/lr0cGrqCewMdjZvJ5OJinQbfmaYNnebdKyY9AQKBgAtv7oygMIBfrXJbkFNEAK8ky9T9jmEV+k1RF+OJMo3mv8GAqbF6kW1nkRO/UVRwd0231pBCbJ67F6SX3aGzWgHgcPfbky5n0sYDaqUgBH5vhwzU0LIkuxlCADIEJ0IV/l61EEhwcVJuob+Tsu3tPu9rdnTrMRMSVTb7WIUYcuShAoGBAJld220uPWS1mwVLDlKXX8skAK0sr6ZiRzlNNPpjPaaNNZX/Tqqfp9yZT/KwserJsY9WtdqHmXXX+0RvdAdPIN+/hcNPU4ehPiERa+gTzIeO2KDb9p/S5VW+yb6wdzrtMB6nWWA7qz4poJln64CrlNRbMqjyRJMQc2qIS0zx1ZUBAoGAfzcapUlX0WtckfCF+ybea8kxmCRXajj+bLM1m75ljg2iPZJ+P6V4WcRhexG1K2kozsqciagyFZaleL7YEfF67Cywvh87UNwOlfBX5ZVESmAlzaJcPGoFfZ3WU1eVBVherkIVDNomTBvsp7vN77HoHnNr59K4S2Qi/mncVnVG5ko=";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问，
    public static String notify_url = "http://www.baidu.com";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://www.baidu.com";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
