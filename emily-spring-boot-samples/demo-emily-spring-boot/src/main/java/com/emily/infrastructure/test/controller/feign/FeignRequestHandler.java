package com.emily.infrastructure.test.controller.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author :  Emily
 * @since :  2024/8/11 上午11:13
 */

@FeignClient(name = "test", url = "http://127.0.0.1:8080/", contextId = "test")
public interface FeignRequestHandler {
    @GetMapping(value = "api/feign/test")
    String get();
}
