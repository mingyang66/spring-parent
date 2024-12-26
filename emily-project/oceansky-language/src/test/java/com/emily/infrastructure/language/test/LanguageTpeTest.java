package com.emily.infrastructure.language.test;

import com.emily.infrastructure.language.i18n.LanguageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2024/12/26 下午11:11
 */
public class LanguageTpeTest {
    @Test
    public void getByCode() {
        Assertions.assertEquals(LanguageType.getByCode("en"), LanguageType.EN);
        Assertions.assertEquals(LanguageType.getByCode("zh-CN"), LanguageType.ZH_CN);
        Assertions.assertEquals(LanguageType.getByCode("zh-TW"), LanguageType.ZH_TW);
    }

    @Test
    public void getByType() {
        Assertions.assertEquals(LanguageType.getByType("EN"), LanguageType.EN);
        Assertions.assertEquals(LanguageType.getByType("ZH_CN"), LanguageType.ZH_CN);
        Assertions.assertEquals(LanguageType.getByType("ZH_TW"), LanguageType.ZH_TW);
    }
}
