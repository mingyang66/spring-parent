package com.emily.infrastructure.security.interceptor;

import com.emily.infrastructure.security.annotation.SecurityOperation;
import com.emily.infrastructure.security.type.SecurityType;
import com.emily.infrastructure.security.utils.SecurityUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * 多语言拦截器
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:21
 */
public class DefaultSecurityMethodInterceptor implements SecurityCustomizer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        SecurityOperation annotation = invocation.getMethod().getAnnotation(SecurityOperation.class);
        if (Arrays.stream(annotation.value()).anyMatch(securityType -> SecurityType.PARAM_DECRYPTION == securityType)) {
            Object[] args = invocation.getArguments();
            if (args.length > 0) {
                SecurityUtils.securityElseGet(args[0], ex -> LOG.error(ex.getMessage(), ex), annotation.removePackClass());
            }
        }
        //执行结果
        Object response = invocation.proceed();
        if (Objects.isNull(response)) {
            return null;
        }
        if (Arrays.stream(annotation.value()).anyMatch(securityType -> SecurityType.RESPONSE_ENCRYPTION == securityType)) {
            //将结果翻译为指定语言类型
            return SecurityUtils.securityElseGet(response, ex -> LOG.error(ex.getMessage(), ex), annotation.removePackClass());
        }
        return response;
    }
}
