package com.yaomy.control.rabbitmq;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.util.concurrent.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2019/10/15 15:46
 * @Version: 1.0
 */
public class Test {

    public static void main(String[] args) throws Exception{
        System.out.println(KeyStore.getDefaultType());
        for(int i=0; i<Security.getProviders().length;i++){
            Provider provider = Security.getProviders()[i];
            System.out.println(provider.getName());
            provider.keySet().iterator().forEachRemaining((a)->{
                System.out.println(a);
            });
        }
        System.out.println(Security.getProviders()[0].getName());
        char[] keyPassphrase = "MySecretPassword".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream("/path/to/client/keycert.p12"), keyPassphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "passphrase".toCharArray());

        char[] trustPassphrase = "rabbitstore".toCharArray();
        KeyStore tks = KeyStore.getInstance("JKS");
        tks.load(new FileInputStream("/path/to/trustStore"), trustPassphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(tks);

        SSLContext c = SSLContext.getInstance("TLSv1.2");
        c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }
}
