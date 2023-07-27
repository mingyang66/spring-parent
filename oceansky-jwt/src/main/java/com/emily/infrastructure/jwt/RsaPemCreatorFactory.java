package com.emily.infrastructure.jwt;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * 生成RSA公钥和私钥PKCS#8
 *
 * @author Emily
 * @since Created in 2023/5/14 4:27 PM
 */
public class RsaPemCreatorFactory {
    /**
     * 公钥文件名
     */
    private static final String PUBLIC_KEY_FILE = "publicKey.pem";
    /**
     * 私钥文件名
     */
    private static final String PRIVATE_KEY_FILE = "privateKey.pem";
    private static final String publicKeyPrefix = "PUBLIC KEY";
    private static final String privateKeyPrefix = "PRIVATE KEY";
    /**
     * 算法
     */
    public static final String ALGORITHM = "RSA";

    public static void create(String directory) throws NoSuchAlgorithmException, IOException {
        // algorithm 指定算法为RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        // 指定密钥长度为2048
        keyPairGenerator.initialize(1024);
        // 生成密钥
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 文件夹不存在，则先创建
        Files.createDirectories(Paths.get(directory));

        try (FileWriter writer = new FileWriter(String.join("", directory, PRIVATE_KEY_FILE));
             PemWriter pemWriter = new PemWriter(writer);
             FileWriter pubFileWriter = new FileWriter(String.join("", directory, PUBLIC_KEY_FILE));
             PemWriter pubPemWriter = new PemWriter(pubFileWriter)) {
            pemWriter.writeObject(new PemObject(privateKeyPrefix, keyPair.getPrivate().getEncoded()));
            pubPemWriter.writeObject(new PemObject(publicKeyPrefix, keyPair.getPublic().getEncoded()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
