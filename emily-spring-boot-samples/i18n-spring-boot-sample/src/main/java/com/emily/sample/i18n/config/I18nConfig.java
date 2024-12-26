package com.emily.sample.i18n.config;

import com.emily.infrastructure.language.i18n.registry.I18nSimpleRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * @author :  Emily
 * @since :  2024/10/31 下午1:58
 */
@Configuration
public class I18nConfig {
    public I18nConfig() {
        I18nSimpleRegistry.bindEn("古北", "gu bei");
        I18nSimpleRegistry.bindEn("渣渣银行", "zha zha bank");
    }
}
