package com.emily.infrastructure.test.controller.otp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.ICredentialRepository;

import java.util.List;


/**
 * @author :  Emily
 * @since :  2023/12/9 10:35 AM
 */

public class Totp {
    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    static {
        gAuth.setCredentialRepository(new ICredentialRepository() {
            @Override
            public String getSecretKey(String userName) {
                return userName;
            }

            @Override
            public void saveUserCredentials(String userName, String secretKey, int validationCode, List<Integer> scratchCodes) {
                //TODO Auto-generated method stub
                System.out.println("userName:" + userName + ",secretKey:" + secretKey + ",validationCode:" + validationCode);
            }
        });
    }

    public static void main(String[] args) {
        String username = "lili";
        GoogleAuthenticatorKey key = gAuth.createCredentials(username);
        System.out.println(key.getKey() + "：" + key.getVerificationCode() + "：" + key.getScratchCodes());
        boolean isCodeValid = gAuth.authorize(key.getKey(), key.getVerificationCode());
        System.out.println("1" + "-" + isCodeValid);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
