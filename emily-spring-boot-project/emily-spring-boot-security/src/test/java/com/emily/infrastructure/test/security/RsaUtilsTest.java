package com.emily.infrastructure.test.security;

import com.emily.infrastructure.security.utils.RsaUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2025/5/11 上午9:41
 */
public class RsaUtilsTest {
    @Test
    void testEncrypt() {
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOWUD3fMflmj1pf6RnoqGekb1H7rD3Nf5iwqRBtWpfPHzC6wdDd5tVQuopnXxxsoIqeAlfxC6qAuUvzSzWsg/E+0Evc0fU4fgukF1pgAsWW0eymjdPyaigWjmDBOlT22LGN3Aup/3hMwOq3VjeBs4qphka6Dh0MI7RtqOdMxFbdZAgMBAAECgYALLoLyWstsokY5cL8uBA7vA6P96oT+IZWcYRKgIkBC92BCheWjLhJe30acJ55S/Elzzxd/CeNTme4A0mOe7CRdxcktJV4UQwoANPkvgyLSf/A4OmCGIRaoMyjHGwgt1oHXRbs+0Wd/bN+WXPoy+xsP7t1DGRrWqHzfs/Ol//QMYQJBAOW47VXIEs1gdNx9n8q8I0ziICKNEwHb3YIXopHU9xbQRIzVV2wkxqY4Z4xo7UmZe77ZOmkLkm3Rq0PyH7jAv98CQQD/1uqMUu+FmixiVq9c6J3uwhYgmuEgUQVWrUjHtzlBEd9uJYKp/oaTEg4ZntudWPqC3YJZhp27i7QX5WEOTY/HAkEAsjk3wK6Zj8b+wzWfbC1sgbCJ1+R1U6LdhpmJofSEwqlQFadKzPb3O2xVQcUCG3C9ZcKoo2i78QGTCwLlA3RfJwJBAK0FigZwqHBwfgfbnCl3YCfytshCNnKGmNevxYLb1Sq+jZ6OW7nf2t9n3IVTPC9D19fdOqSVN+N4bcmjZWoo5k0CQHqDNKyZ4PJSsoPzH+USh1sPClNGSteeNzfSwnHpV2u9PcKngPo0RyKqWo/1FyIfqbJCF9hvH2cTWRrFcXkMF9k=";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDllA93zH5Zo9aX+kZ6KhnpG9R+6w9zX+YsKkQbVqXzx8wusHQ3ebVULqKZ18cbKCKngJX8QuqgLlL80s1rIPxPtBL3NH1OH4LpBdaYALFltHspo3T8mooFo5gwTpU9tixjdwLqf94TMDqt1Y3gbOKqYZGug4dDCO0bajnTMRW3WQIDAQAB";
        String encrypted = RsaUtils.encrypt("123456", publicKey);
        String decrypted = RsaUtils.decrypt(encrypted, privateKey);
        Assertions.assertEquals("123456", decrypted);
    }
}
