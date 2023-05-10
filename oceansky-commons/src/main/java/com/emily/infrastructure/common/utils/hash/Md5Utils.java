package com.emily.infrastructure.common.utils.hash;

import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Description: md5进行hash计算
 * @Author: Emily
 * @create: 2022/1/8
 */
public class Md5Utils {
    public static final String MD5 = "MD5";

    /**
     * 生成字符串的MD5 hash值
     *
     * @param input
     * @return
     */
    public static String computeMd5Hash(String input) {
        //参数校验
        if (StringUtils.isEmpty(input)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            md.update(input.getBytes());
            byte[] digest = md.digest();
            BigInteger bi = new BigInteger(1, digest);
            String hashText = bi.toString(16);
            while (hashText.length() < 32) {
                hashText = StringUtils.join("0", hashText);
            }
            return hashText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 计算文件的 md5 hash值
     */
    public static String computeMd5Hash(File file) {
        //摘要输入流
        DigestInputStream din = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            //第一个参数是一个输入流,第二个是要与此流关联的消息摘要
            din = new DigestInputStream(new BufferedInputStream(new FileInputStream(file)), md5);

            byte[] b = new byte[1024];
            if (din.read(b) != -1) {
                byte[] digest = md5.digest();
                return DatatypeConverter.printHexBinary(digest);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (din != null) {
                    din.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
