package com.emily.infrastructure.test.controller.otp;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.HOTPGenerator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author :  Emily
 * @since :  2024/3/1 11:08 AM
 */
@RestController
@RequestMapping("api/v3/hotp")
public class HotpControllerV3 {
    // To generate a secret with 160 bits
    //private static byte[] secret = SecretGenerator.generate();
    private static byte[] secret = "lixiaoyanxians".getBytes(StandardCharsets.UTF_8);

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

    @GetMapping("generatorCode")
    public String generatorCode(@RequestParam("counter") int counter) throws URISyntaxException {
        HOTPGenerator hotp = new HOTPGenerator.Builder(secret)
                .withPasswordLength(6)
                .withAlgorithm(HMACAlgorithm.SHA1)
                .build();
        String code = hotp.generate(counter);
        URI uri = hotp.getURI(counter, "立小言先森");
        return code + "|" + getOtpAuthURL(uri.toString());
    }

    @GetMapping("verify")
    public boolean verify(@RequestParam("code") String code, @RequestParam("counter") int counter) {
        HOTPGenerator hotp = new HOTPGenerator.Builder(secret)
                .withPasswordLength(6)
                .withAlgorithm(HMACAlgorithm.SHA1)
                .build();
        // To verify a token:
        //boolean isValid = hotp.verify(code, counter);

        // Or verify with a delay window
        // delayWindow=2 意思是counter值在[counter-2, counter+2]之间都认为是有效的
        boolean isValid = hotp.verify(code, counter, 2);
        return isValid;
    }
}
