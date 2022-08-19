package com.emily.cloud.test.fegin.controller;

import com.emily.cloud.test.fegin.handler.CustomFeignHandler;
import com.emily.cloud.test.fegin.handler.DefaultFeignHandler;
import com.emily.infrastructure.core.entity.BaseResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description :  自定义Feign控制器
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/4 10:48 上午
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
    public BaseResponse<String> getCustom(HttpServletRequest request) {
        int timeout = NumberUtils.toInt(request.getParameter("timeout"), 0);
        return customFeignHandler.getCustom(timeout);
    }


    @GetMapping("connect")
    public String connect(int timeout) throws InterruptedException {
        Thread.sleep(timeout);
        return "默认";
    }

    @GetMapping("custom")
    public String custom(int timeout) throws InterruptedException {
        Thread.sleep(timeout);
        return "自定义";
    }
}