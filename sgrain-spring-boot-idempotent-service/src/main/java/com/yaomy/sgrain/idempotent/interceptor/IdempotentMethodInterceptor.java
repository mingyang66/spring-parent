package com.yaomy.sgrain.idempotent.interceptor;

import com.yaomy.sgrain.common.enums.AppHttpStatus;
import com.yaomy.sgrain.common.utils.Md5Utils;
import com.yaomy.sgrain.common.utils.RequestUtils;
import com.yaomy.sgrain.exception.business.BusinessException;
import com.yaomy.sgrain.idempotent.annotation.Idempotent;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 防止重复提交AOP拦截器
 */
public class IdempotentMethodInterceptor implements MethodInterceptor {
    /**
     * 防止接口重复提交header参数
     */
    private static final String AUTHENTICATION = "Authentication";
    /**
     * 分号
     */
    private static final String SEMICOLON = ":";
    /**
     * Redisson 客户端对象
     */
    private RedissonClient redissonClient;
    private RedisTemplate<Object, Object> redisTemplate;
    private DefaultRedisScript<Long> redisScript;

    public IdempotentMethodInterceptor(RedissonClient redissonClient, RedisTemplate<Object, Object> redisTemplate, DefaultRedisScript<Long> redisScript){
        this.redissonClient = redissonClient;
        this.redisTemplate = redisTemplate;
        this.redisScript = redisScript;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //判定方法上是否被幂等性注解标注
        if(!method.isAnnotationPresent(Idempotent.class)){
            return invocation.proceed();
        }
        //获取幂等性注解对象
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        //幂等性未启用
        if(!idempotent.enable()){
            return invocation.proceed();
        }
        HttpServletRequest request = RequestUtils.getRequest();
        //客户端发送的防止接口重复提交header参数
        String authentication = request.getHeader(AUTHENTICATION);
        if(StringUtils.isEmpty(authentication)){
            throw new BusinessException(AppHttpStatus.IDEMPOTENT_EXCEPTION.getStatus(), "幂等性验证Header(Authentication)不可为空！");
        }
        //--------------------TOKEN验证模式-------------------------
        if(idempotent.type().equals(Idempotent.Type.TOKEN)){
            Long data = redisTemplate.execute(redisScript, Collections.singletonList(authentication));
            if(ObjectUtils.isNotEmpty(data) && data == 1L){
                return invocation.proceed();
            } else {
                throw new BusinessException(AppHttpStatus.IDEMPOTENT_EXCEPTION);
            }
        }
        //--------------------TOKEN_AND_URL验证模式--------------------
        //请求URL
        String url = request.getRequestURI();
        //对当前用户的请求加锁key
        String key = Md5Utils.computeMD5Hash(StringUtils.join(authentication, SEMICOLON, url));
        //获取分布式锁对象
        RLock lock = redissonClient.getLock(key);
        //尝试获取分布式锁
        if(lock.tryLock()){
            try {
                return invocation.proceed();
            } finally {
                lock.unlock();
            }
        }
        throw new BusinessException(AppHttpStatus.IDEMPOTENT_EXCEPTION);
    }
}
