package com.emily.infrastructure.datasource.test;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static com.alibaba.druid.filter.config.ConfigTools.genKeyPair;
import static com.emily.infrastructure.common.utils.hash.DesUtils.encrypt;

/**
 * @Description : 数据库密码生成
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/27 2:18 PM
 */
public class RsaToolTest {
    @Test
    public void test() throws NoSuchAlgorithmException, NoSuchProviderException {
        String password = "123";
        String[] arr = genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + encrypt(arr[0], password));
    }
}
