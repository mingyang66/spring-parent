package com.emily.infrastructure.test.controller.otp;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.date.DateConvertUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.Duration;
import java.time.Instant;

/**
 * @author :  Emily
 * @since :  2024/2/29 3:32 PM
 */
@RestController
@RequestMapping("api/v2/totp")
public class TotpControllerV2 {
    private static final byte[] secret = SecretGenerator.generate();

    @GetMapping("generatorCode")
    public String generatorCode() {
        TOTPGenerator totp = getTOTPGenerator();
        System.out.println(new String(secret));
        return totp.at(Instant.now().plusSeconds(30*3));
    }

    @GetMapping("verify")
    public boolean verify(@RequestParam("code") String code) {
        TOTPGenerator totp = getTOTPGenerator();
        //return totp.verify(code);
        // 验证延迟几个窗口时间有效
        return totp.verify(code,1);
    }

    @GetMapping("getUrl")
    public String getUrl() {
        TOTPGenerator totp = getTOTPGenerator();
        try {
            return getOtpAuthURL(totp.getURI("立小言先森", new String(secret)).toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public TOTPGenerator getTOTPGenerator() {
        return new TOTPGenerator.Builder(secret)
                .withHOTPGenerator(builder -> {
                    builder.withPasswordLength(6);
                    builder.withAlgorithm(HMACAlgorithm.SHA1); // SHA256 and SHA512 are also supported
                })
                .withPeriod(Duration.ofSeconds(30))
                .build();
    }

    private static String internalURLEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new IllegalArgumentException("UTF-8 encoding is not supported by URLEncoder.", var2);
        }
    }

    public static String getOtpAuthURL(String url) {
        return String.format("https://api.qrserver.com/v1/create-qr-code/?data=%s&size=300x300&ecc=M&margin=1", internalURLEncode(url));
    }
}
