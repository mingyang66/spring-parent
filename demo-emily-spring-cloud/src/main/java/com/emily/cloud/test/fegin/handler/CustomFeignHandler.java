package com.emily.cloud.test.fegin.handler;

import com.emily.cloud.test.fegin.Custom;
import com.emily.infrastructure.autoconfigure.entity.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 自定义handler请求
 *
 * @author Emily
 * @since Created in 2022/8/4 10:47 上午
 */
@FeignClient(value = "custom", url = "http://127.0.0.1:9000/api/feign", contextId = "custom")
public interface CustomFeignHandler {
    /**
     * 自定义超时请求
     */
    @PostMapping("custom")
    BaseResponse<Custom> getCustom(Custom custom);
}
