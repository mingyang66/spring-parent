package com.yaomy.sgrain.submit.interceptor;

import com.yaomy.sgrain.common.enums.SgrainHttpStatus;
import com.yaomy.sgrain.common.utils.Md5Utils;
import com.yaomy.sgrain.common.utils.RequestUtils;
import com.yaomy.sgrain.exception.business.BusinessException;
import com.yaomy.sgrain.submit.annotation.NoRepeatSubmit;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @program: spring-parent
 * @description: 防止重复提交AOP拦截器
 * @author: 姚明洋
 * @create: 2020/03/26
 */
public class RepeatSubmitMethodInterceptor implements MethodInterceptor {
    /**
     * 防止接口重复提交header参数
     */
    private static final String REPEAT_SUBMIT_ID = "REPEAT_SUBMIT_ID";
    /**
     * 分号
     */
    private static final String SEMICOLON = ":";
    /**
     * Redisson 客户端对象
     */
    private RedissonClient redissonClient;

    public RepeatSubmitMethodInterceptor(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if(!method.isAnnotationPresent(NoRepeatSubmit.class)){
            return invocation.proceed();
        }
        NoRepeatSubmit noRepeatSubmit = method.getAnnotation(NoRepeatSubmit.class);
        if(!noRepeatSubmit.enable()){
            return invocation.proceed();
        }
        HttpServletRequest request = RequestUtils.getRequest();
        //客户端发送的防止接口重复提交header参数
        String repeatSubmitId = request.getHeader(REPEAT_SUBMIT_ID);
        if(StringUtils.isEmpty(repeatSubmitId)){
            throw new BusinessException(SgrainHttpStatus.REPEAT_SUBMIT_EXCEPTION.getStatus(), "接口重复提交Header(REPEAT_SUBMIT_ID)不可为空");
        }
        //请求URL
        String url = request.getRequestURI();
        //对当前用户的请求加锁key
        String key = Md5Utils.computeMD5Hash(StringUtils.join(repeatSubmitId, SEMICOLON, url));
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
        throw new BusinessException(SgrainHttpStatus.REPEAT_SUBMIT_EXCEPTION);
    }
}
