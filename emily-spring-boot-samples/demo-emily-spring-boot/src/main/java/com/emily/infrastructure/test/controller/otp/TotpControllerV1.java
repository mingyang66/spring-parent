package com.emily.infrastructure.test.controller.otp;


import com.emily.infrastructure.date.DatePatternInfo;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL;

@RestController
@RequestMapping("api/v1/totp")
public class TotpControllerV1 {

    @PostMapping("generatorCode")
    public String generate() {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePatternInfo.HH_MM_SS));
        GoogleAuthenticatorConfig config = generateConfig();
        // 创建 GoogleAuthenticator 实例
        GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
        // 生成谷歌身份验证器密钥
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        // 秘钥
        String secretKey = key.getKey();
        // 获取密钥字符串和URL
        String otpAuthURL = getOtpAuthURL("立小言先森", now, key);
        System.out.println("密钥： " + secretKey);
        System.out.println("身份验证器URL： " + otpAuthURL);
        System.out.println("VerificationCode" + key.getVerificationCode());
        List<Integer> list = key.getScratchCodes();
        System.out.println("ScratchCodes:" + list.contains(key.getVerificationCode()));
        return key.getVerificationCode() + "|" + secretKey + "|" + otpAuthURL;
    }


    @PostMapping("verify")
    public boolean auth2(@RequestParam("key") String key, @RequestParam("code") int code) {
        GoogleAuthenticatorConfig config = generateConfig();
        // 创建 GoogleAuthenticator 实例
        GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
        // 验证身份验证令牌
        return gAuth.authorize(key, code);
    }


    private static GoogleAuthenticatorConfig generateConfig() {
        //配置生成器
        return new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                //验证码位数，默认：6位
                .setCodeDigits(6)
                //时间窗口大小，默认：3
                .setWindowSize(3)
                .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(30))
                .build();
    }

    private static String internalURLEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new IllegalArgumentException("UTF-8 encoding is not supported by URLEncoder.", var2);
        }
    }

    public static String getOtpAuthURL(String issuer, String accountName, GoogleAuthenticatorKey credentials) {
        return String.format("https://api.qrserver.com/v1/create-qr-code/?data=%s&size=300x300&ecc=M&margin=1", internalURLEncode(getOtpAuthTotpURL(issuer, accountName, credentials)));
    }
}
