package com.sgrain.boot.autoconfigure.aop.interceptor;

import ch.qos.logback.classic.Level;
import com.sgrain.boot.autoconfigure.aop.log.event.LogAop;
import com.sgrain.boot.autoconfigure.aop.log.event.LogApplicationEvent;
import com.sgrain.boot.common.utils.RequestUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
public class LogAopMethodInterceptor implements MethodInterceptor {

    private ApplicationEventPublisher publisher;

    public LogAopMethodInterceptor(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * 拦截接口日志
     *
     * @param invocation 接口方法切面连接点
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        LogAop logAop = new LogAop(invocation, RequestUtils.getRequest());
        //新建计时器并开始计时
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            //调用升级的action方法
            Object result = invocation.proceed();
            //暂停计时
            stopWatch.stop();

            if (ObjectUtils.isNotEmpty(result) && (result instanceof ResponseEntity)) {
                Object resultBody = ((ResponseEntity) result).getBody();
                logAop.setResult(resultBody);
            } else {
                logAop.setResult(result);
            }
            //日志级别
            logAop.setLogLevel(Level.INFO.levelStr);
            //耗时
            logAop.setSpendTime((stopWatch.getTime() == 0) ? 1 : stopWatch.getTime());
            //发布事件
            publisher.publishEvent(new LogApplicationEvent(logAop));

            return result;
        } catch (Throwable e) {
            //暂停计时
            if (stopWatch.isStarted() || stopWatch.isSuspended()) {
                stopWatch.stop();
            }
            //日志级别
            logAop.setLogLevel(Level.ERROR.levelStr);
            //耗时
            logAop.setSpendTime((stopWatch.getTime() == 0) ? 1 : stopWatch.getTime());
            //异常
            logAop.setThrowable(e);
            //发布事件
            publisher.publishEvent(new LogApplicationEvent(logAop));

            throw e;
        }
    }
}
