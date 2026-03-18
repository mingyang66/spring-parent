package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.RsaUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

/**
 * @author :  Emily
 * @since :  2025/5/11 上午9:41
 */
public class RsaUtilsTest {
    @Test
    void testEncrypt() throws NoSuchAlgorithmException {
        KeyPair keyPair = RsaUtils.generateKeyPair(2048);
        String privateKey = RsaUtils.getPrivateKeyString(keyPair);
       // System.out.println(privateKey);
        String publicKey = RsaUtils.getPublicKeyString(keyPair);
       // System.out.println(publicKey);
        String str = "123456789...";
        System.out.println(str.getBytes(StandardCharsets.UTF_8).length);
        String encrypted = RsaUtils.encrypt(str, publicKey);
        String decrypted = RsaUtils.decrypt(encrypted, privateKey);
        Assertions.assertEquals(str, decrypted);

    }
}
