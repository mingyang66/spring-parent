package com.sgrain.boot.autoconfigure.aop.interceptor;


import com.sgrain.boot.autoconfigure.aop.annotation.Idempotent;
import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * 防止重复提交AOP拦截器
 */
public class IdempotentMethodInterceptor implements MethodInterceptor {
    /**
     * 防止接口重复提交header参数
     */
    private static final String AUTHENTICATION = "Authentication";
    /**
     * Redis 客户端对象
     */
    private RedisTemplate<Object, Object> redisTemplate;

    public IdempotentMethodInterceptor(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        //获取幂等性注解对象
        Idempotent idempotent = invocation.getMethod().getAnnotation(Idempotent.class);
        //幂等性未启用
        if (!idempotent.enable()) {
            return invocation.proceed();
        }
        HttpServletRequest request = RequestUtils.getRequest();
        //客户端发送的防止接口重复提交header参数
        String authentication = request.getHeader(AUTHENTICATION);
        if (StringUtils.isEmpty(authentication)) {
            throw new BusinessException(AppHttpStatus.API_IDEMPOTENT_EXCEPTION.getStatus(), "幂等性验证Header(Authentication)不可为空！");
        }
        boolean delFlag = redisTemplate.delete(StringUtils.join("idempotent", CharacterUtils.COLON_EN, authentication));
        if (!delFlag) {
            throw new BusinessException(AppHttpStatus.API_IDEMPOTENT_EXCEPTION);
        }
        return invocation.proceed();
    }
}
