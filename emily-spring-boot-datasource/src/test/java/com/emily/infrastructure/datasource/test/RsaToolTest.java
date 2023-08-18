package com.emily.infrastructure.datasource.test;

import org.junit.Test;

import static com.alibaba.druid.filter.config.ConfigTools.encrypt;
import static com.alibaba.druid.filter.config.ConfigTools.genKeyPair;

/**
 * 数据库密码生成
 *
 * @author Emily
 * @since Created in 2023/5/27 2:18 PM
 */
public class RsaToolTest {
    @Test
    public void test() throws Exception {
        String password = "123";
        String[] arr = genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + encrypt(arr[0], password));
    }
}
