package com.emily.infrastructure.security.interceptor;

import com.emily.infrastructure.security.annotation.SecurityOperation;
import com.emily.infrastructure.security.type.SecurityType;
import com.emily.infrastructure.security.utils.SecurityUtils;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Arrays;

/**
 * 多语言拦截器
 *
 * @author :  Emily
 * @since :  2024/10/31 上午10:21
 */
public class DefaultSecurityMethodInterceptor implements SecurityCustomizer {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        SecurityOperation annotation = invocation.getMethod().getAnnotation(SecurityOperation.class);
        if (Arrays.stream(annotation.value()).anyMatch(securityType -> SecurityType.REQUEST == securityType)) {
            Object[] args = invocation.getArguments();
            if (args.length > 0) {
                SecurityUtils.security(args[0], annotation.removePackClass());
            }
        }
        //执行结果
        Object response = invocation.proceed();
        if (Arrays.stream(annotation.value()).anyMatch(securityType -> SecurityType.RESPONSE == securityType)) {
            //将结果翻译为指定语言类型
            return SecurityUtils.security(response, annotation.removePackClass());
        }
        return response;
    }
}
