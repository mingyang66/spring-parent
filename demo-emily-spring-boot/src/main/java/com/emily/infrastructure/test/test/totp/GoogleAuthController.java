package com.emily.infrastructure.test.test.totp;


import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL;

@RestController
@RequestMapping("api/googleAuth")
public class GoogleAuthController {
    //@ApiOperation(value = "生成google身份验证器Key")
    @PostMapping("generate")
    public String generate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        String now = LocalDateTime.now().format(dtf);
        var config = generateConfig();
        // 创建 GoogleAuthenticator 实例
        GoogleAuthenticator gAuth = new GoogleAuthenticator(config);
        // 生成谷歌身份验证器密钥
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();
        // 获取密钥字符串和URL
        String otpAuthURL = getOtpAuthURL("HafooApp", now, key);
        System.out.println("密钥： " + secretKey);
        System.out.println("身份验证器URL： " + otpAuthURL);
        System.out.println("VerificationCode" + key.getVerificationCode());
        return secretKey + "|" + otpAuthURL;
    }

    //@ApiOperation(value = "验证")
    @PostMapping("auth/{key}/{validateCode}")
    public boolean auth2(@PathVariable("key") String key, @PathVariable("validateCode") int validateCode) {
        // 创建 GoogleAuthenticator 实例
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        // 验证身份验证令牌
        int verificationCode = validateCode; // 用户提供的身份验证令牌
        boolean isCodeValid = gAuth.authorize(key, verificationCode);
        return isCodeValid;
    }

    private GoogleAuthenticatorConfig generateConfig() {
        //配置生成器
        var builder = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                .setCodeDigits(6)
                .setWindowSize(3)
                .setTimeStepSizeInMillis(TimeUnit.SECONDS.toMillis(20));//10s的时长
        GoogleAuthenticatorConfig config = builder.build();
        return config;
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
