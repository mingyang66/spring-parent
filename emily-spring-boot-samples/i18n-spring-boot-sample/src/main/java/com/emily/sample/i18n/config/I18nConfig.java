package com.emily.sample.i18n.config;

import com.emily.infrastructure.language.convert.I18nCache;
import org.springframework.context.annotation.Configuration;

/**
 * @author :  姚明洋
 * @since :  2024/10/31 下午1:58
 */
@Configuration
public class I18nConfig {
    public I18nConfig() {
        I18nCache.bindEn("古北", "gu bei");
        I18nCache.bindEn("渣渣银行", "zha zha bank");
    }
}
