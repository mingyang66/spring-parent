package com.emily.infrastructure.language.test;

import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.registry.I18nSimpleRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2024/12/26 下午11:24
 */
class I18nSimpleRegistryTest {
    @BeforeAll
    public static void setUp() {
        I18nSimpleRegistry.getEnRegistry().put("田润叶", "runye tian");
        I18nSimpleRegistry.getEnRegistry().put("孙少安", "shaoan sun");
        I18nSimpleRegistry.getZhRegistry().put("田润叶", "田潤葉");
        I18nSimpleRegistry.getZhRegistry().put("孙少安", "孙少安");
    }

    @Test
    public void acquireTest() {
        Assertions.assertEquals(I18nSimpleRegistry.acquire("田润叶", LanguageType.EN), "runye tian");
        Assertions.assertEquals(I18nSimpleRegistry.acquire("孙少安", LanguageType.EN), "shaoan sun");
        Assertions.assertEquals(I18nSimpleRegistry.acquire("田润叶", LanguageType.ZH_TW), "田潤葉");
        Assertions.assertEquals(I18nSimpleRegistry.acquire("孙少安", LanguageType.ZH_TW), "孙少安");

        Assertions.assertEquals(I18nSimpleRegistry.acquire("田润叶", "en"), "runye tian");
        Assertions.assertEquals(I18nSimpleRegistry.acquire("孙少安", "en"), "shaoan sun");
        Assertions.assertEquals(I18nSimpleRegistry.acquire("田润叶", "zh-TW"), "田潤葉");
        Assertions.assertEquals(I18nSimpleRegistry.acquire("孙少安", "ZH-TW"), "孙少安");
    }
}
