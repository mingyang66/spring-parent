/*
package com.emily.infrastructure.test.test;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

*/
/**
 * @author :  Emily
 * @since :  2023/12/9 10:35 AM
 *//*

public class Totp {
    public static void main(String[] args) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        for (int i = 0; i < 100; i++) {
            int code = gAuth.getTotpPassword(key.getKey(), System.currentTimeMillis());
            System.out.println(key.getKey());
            System.out.println(code);
            boolean isCodeValid = gAuth.authorize(key.getKey(), code);
            System.out.println(i + "-" + isCodeValid);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
*/
