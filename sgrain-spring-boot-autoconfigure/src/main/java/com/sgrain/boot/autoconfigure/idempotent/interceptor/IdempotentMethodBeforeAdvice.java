package com.sgrain.boot.autoconfigure.idempotent.interceptor;


import com.sgrain.boot.autoconfigure.idempotent.annotation.ApiIdempotent;
import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 防止重复提交AOP拦截器
 */
public class IdempotentMethodBeforeAdvice implements MethodBeforeAdvice {
    /**
     * 防止接口重复提交header参数
     */
    private static final String AUTHENTICATION = "Authentication";
    /**
     * Redis 客户端对象
     */
    private StringRedisTemplate stringRedisTemplate;

    public IdempotentMethodBeforeAdvice(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        //获取幂等性注解对象
        ApiIdempotent idempotent = method.getAnnotation(ApiIdempotent.class);
        //幂等性未启用
        if (!idempotent.enable()) {
            return;
        }
        HttpServletRequest request = RequestUtils.getRequest();
        //客户端发送的防止接口重复提交header参数
        String authentication = request.getHeader(AUTHENTICATION);
        if (StringUtils.isEmpty(authentication)) {
            throw new BusinessException(AppHttpStatus.API_IDEMPOTENT_EXCEPTION.getStatus(), "幂等性验证Header(Authentication)不可为空！");
        }
        boolean delFlag = stringRedisTemplate.delete(StringUtils.join("idempotent", CharacterUtils.COLON_EN, authentication));
        if (!delFlag) {
            throw new BusinessException(AppHttpStatus.API_IDEMPOTENT_EXCEPTION);
        }
    }

}
