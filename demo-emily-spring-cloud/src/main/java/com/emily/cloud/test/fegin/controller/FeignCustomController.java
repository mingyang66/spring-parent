package com.emily.cloud.test.fegin.controller;

import com.emily.cloud.test.fegin.Custom;
import com.emily.cloud.test.fegin.handler.CustomFeignHandler;
import com.emily.cloud.test.fegin.handler.DefaultFeignHandler;
import com.emily.infrastructure.core.entity.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 *  自定义Feign控制器
 * @author  Emily
 * @since  Created in 2022/8/4 10:48 上午
 */
@RestController
@RequestMapping("api/feign")
public class FeignCustomController {
    @Autowired
    private DefaultFeignHandler defaultFeignHandler;
    @Autowired
    private CustomFeignHandler customFeignHandler;

    @GetMapping("getDefault")
    public BaseResponse<String> getDefault(HttpServletRequest request) {
        int timeout = NumberUtils.toInt(request.getParameter("timeout"), 0);
        return defaultFeignHandler.getConnect(timeout);
    }

    @GetMapping("getCustom")
    public BaseResponse<Custom> getCustom(HttpServletRequest request) {
        int timeout = NumberUtils.toInt(request.getParameter("timeout"), 0);
        Custom custom = new Custom();
        custom.setEmail("laobai@foxmail.com");
        custom.setUsername("化峥");
        return customFeignHandler.getCustom(custom);
    }


    @GetMapping("connect")
    public String connect(int timeout) throws InterruptedException {
        Thread.sleep(timeout);
        return "默认";
    }

    @PostMapping("custom")
    public Custom custom(@RequestBody Custom custom1) throws InterruptedException {
        Custom custom = new Custom();
        custom.setUsername("柯镇恶");
        custom.setEmail("kezhene@foxmail.com");
        return custom;
    }
}
