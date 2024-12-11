package com.emily.sample.tracing.config;

import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.sample.tracing.TracingService;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

/**
 * @author :  Emily
 * @since :  2024/12/10 下午4:32
 */
@Configuration
public class TracingStartUp {
    private final TracingService tracingService;

    public TracingStartUp(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @PostConstruct
    public void startUp() {
        //System.out.println(Thread.currentThread().getId() + "-------------startUp start------------：" + LocalContextHolder.current().getTraceId());
        tracingService.config();
        tracingService.config();
        //System.out.println(Thread.currentThread().getId() + "-------------startUp end------------：" + LocalContextHolder.current().getTraceId());
    }


}
