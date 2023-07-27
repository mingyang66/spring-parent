package com.emily.infrastructure.jwt.test;

import com.emily.infrastructure.jwt.RsaPemCreatorFactory;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 *  公钥和私钥创建帮助类
 * @author  Emily
 * @since  Created in 2023/5/14 5:05 PM
 */
public class RsaPemCreatorHelperTest {
    @Test
    public void test() throws NoSuchAlgorithmException, IOException {
        String DIRECTORY = "src/main/resources/rsa/";
        RsaPemCreatorFactory.create(DIRECTORY);
    }
}
