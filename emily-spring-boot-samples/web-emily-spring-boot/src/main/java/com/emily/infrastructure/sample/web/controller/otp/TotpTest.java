package com.emily.infrastructure.sample.web.controller.otp;

import com.bastiaanjansen.otp.HMACAlgorithm;
import com.bastiaanjansen.otp.SecretGenerator;
import com.bastiaanjansen.otp.TOTPGenerator;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.time.Duration;

/**
 * @author :  Emily
 * @since :  2024/2/29 3:10 PM
 */
public class TotpTest {
    public static void main(String[] args) {
        // Generate a secret (or use your own secret)
        byte[] secret = SecretGenerator.generate();

        TOTPGenerator totp = new TOTPGenerator.Builder(secret)
                .withHOTPGenerator(builder -> {
                    builder.withPasswordLength(6);
                    builder.withAlgorithm(HMACAlgorithm.SHA1); // SHA256 and SHA512 are also supported
                })
                .withPeriod(Duration.ofSeconds(30))
                .build();

        try {
            String code = totp.now();

            // To verify a token:
            boolean isValid = totp.verify(code);
            System.out.println(code);
            System.out.println(isValid);


            //TOTPGenerator totpGenerator = new TOTPGenerator.Builder(secret).build();

            URI uri = totp.getURI("立小言先森", "1008611"); // otpauth://totp/issuer:account?period=30&digits=6&secret=SECRET&algorithm=SHA1
            System.out.println(uri);
            System.out.println(getOtpAuthURL(uri.toString()));
        } catch (IllegalStateException e) {
            // Handle error
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
