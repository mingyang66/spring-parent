package com.emily.infrastructure.tracing.interceptor;

import com.alibaba.ttl.TtlCallable;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import jakarta.annotation.Nonnull;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Future;

/**
 * 非servlet上下文执行完成后移除拦截器，对servlet上下文场景同样适用
 *
 * @author :  Emily
 * @since :  2024/12/10 下午3:39
 */
public class DefaultTracingMethodInterceptor implements TracingCustomizer {
    private final Logger LOG = LoggerFactory.getLogger(DefaultTracingMethodInterceptor.class);
    private final ThreadPoolTaskExecutor taskExecutor;

    public DefaultTracingMethodInterceptor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        Future<Object> future = taskExecutor.submit(TtlCallable.get(() -> {
            try {
                return invocation.proceed();
            } catch (Throwable e) {
                LOG.error(e.getMessage(), e);
                throw new IllegalAccessException("invoke method error");
            } finally {
                LocalContextHolder.unbind(true);
            }
        }));
        return future.get();
    }
}
