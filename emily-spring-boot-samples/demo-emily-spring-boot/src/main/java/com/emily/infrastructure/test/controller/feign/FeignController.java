package com.emily.infrastructure.test.controller.feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  Emily
 * @since :  2024/8/14 下午1:18
 */
@RestController
public class FeignController {
    private final FeignRequestHandler feignRequestHandler;

    public FeignController(FeignRequestHandler feignRequestHandler) {
        this.feignRequestHandler = feignRequestHandler;
    }

    @GetMapping("api/feign/get")
    public String get() {
        return feignRequestHandler.get();
    }

    @GetMapping("api/feign/test")
    public String test() {
        return "test";
    }
}
