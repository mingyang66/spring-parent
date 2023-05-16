package com.emily.infrastructure.jwt.test;

import com.emily.infrastructure.jwt.RsaPemCreatorHelper;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @Description :  公钥和私钥创建帮助类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 5:05 PM
 */
public class RsaPemCreatorHelperTest {
    @Test
    public void test() throws NoSuchAlgorithmException, IOException {
        String DIRECTORY = "oceansky-jwt/src/main/resources/rsa/";
        RsaPemCreatorHelper.create(DIRECTORY);
    }
}
