package com.emily.infrastructure.i18n.interceptor;

import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.i18n.annotation.I18nOperation;
import com.emily.infrastructure.language.convert.I18nUtils;
import com.emily.infrastructure.language.convert.LanguageType;
import com.otter.infrastructure.servlet.RequestUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 多语言拦截器
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:21
 */
public class DefaultI18nMethodInterceptor implements I18nCustomizer {
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(I18nOperation.class)) {
            //执行结果
            Object response = invocation.proceed();
            //语言类型
            LanguageType languageType = LanguageType.getByCode(RequestUtils.getHeader(HeaderInfo.LANGUAGE));
            //将结果翻译为指定语言类型
            return I18nUtils.acquire(response, languageType);
        }
        return invocation.proceed();
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
