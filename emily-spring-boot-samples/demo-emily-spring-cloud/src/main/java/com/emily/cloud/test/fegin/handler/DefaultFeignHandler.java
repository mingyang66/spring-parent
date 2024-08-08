package com.emily.cloud.test.fegin.handler;

import com.emily.infrastructure.autoconfigure.entity.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 默认handler请求
 *
 * @author Emily
 * @since Created in 2022/8/4 10:46 上午
 */
@FeignClient(value = "connect", url = "http://127.0.0.1:9000/api/feign")
public interface DefaultFeignHandler {
    /**
     * 默认超时请求
     */
    @GetMapping("connect")
    BaseResponse<String> getConnect(@RequestParam("timeout") int timeout);
}
