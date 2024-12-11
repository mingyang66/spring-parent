package com.emily.sample.tracing;

import com.emily.infrastructure.tracing.annotation.TracingOperation;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import org.springframework.stereotype.Service;

/**
 * @author :  Emily
 * @since :  2024/12/10 下午4:50
 */
@Service
public class TracingService {
    @TracingOperation
    public void config() {
        System.out.println(Thread.currentThread().getId() + "-------------config start------------：" + LocalContextHolder.current().getTraceId());
        System.out.println(Thread.currentThread().getId() + "-------------config end------------：" + LocalContextHolder.current().getTraceId());
    }
}
