package com.emily.sample.tracing;

import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  Emily
 * @since :  2024/12/11 上午9:59
 */
@RestController
public class TracingController {
    private final TracingService tracingService;

    public TracingController(TracingService tracingService) {
        this.tracingService = tracingService;
    }

    @GetMapping("api/tracing/test")
    public void test() {
        System.out.println(Thread.currentThread().getId() + "-------------Controller start------------：" + LocalContextHolder.current().getTraceId());
        tracingService.config();
        tracingService.config();
        System.out.println(Thread.currentThread().getId() + "-------------Controller End------------：" + LocalContextHolder.current().getTraceId());
        System.out.println(Thread.currentThread().getId() + "-------------Controller start------------：" + LocalContextHolder.current().isServlet());
    }
}
