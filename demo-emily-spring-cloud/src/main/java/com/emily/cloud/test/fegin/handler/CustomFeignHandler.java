package com.emily.cloud.test.fegin.handler;

import com.emily.infrastructure.core.entity.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description :  自定义handler请求
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/4 10:47 上午
 */
@FeignClient(value = "custom", url = "http://127.0.0.1:9000/api/feign", contextId = "custom")
public interface CustomFeignHandler {
    /**
     * 自定义超时请求
     */
    @GetMapping("custom")
    BaseResponse<String> getCustom(@RequestParam("timeout")  int timeout);
}
