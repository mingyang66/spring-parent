package com.emily.infrastructure.web.exception.helper;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.registry.I18nSimpleRegistry;
import com.otter.infrastructure.servlet.RequestUtils;
import org.springframework.util.ClassUtils;

import java.text.MessageFormat;

/**
 * I18n多语言支持
 *
 * @author :  Emily
 * @since :  2025/5/18 下午4:35
 */
public class MessageHelper {
    private static final boolean COMMONS_SENSITIZE_AVAILABLE = ClassUtils.isPresent("com.emily.infrastructure.language.i18n.I18nUtils", MessageHelper.class.getClassLoader());

    public static String getMessage(String message, Object... args) {
        String msg = COMMONS_SENSITIZE_AVAILABLE ? I18nSimpleRegistry.acquire(message, RequestUtils.getHeaderOrDefault(HeaderInfo.LANGUAGE, LanguageType.ZH_CN.getCode())) : message;
        if (StringUtils.isNotBlank(msg) && args.length > 0) {
            return MessageFormat.format(msg, args);
        }
        return msg;
    }
}
