package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.core.entity.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @program: spring-parent
 * @description: http控制器
 * @author: Emily
 * @create: 2021/11/11
 */
@RequestMapping("api/http")
@RestController
public class HttpClientController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("get")
    public BaseResponse get() {
        return restTemplate.getForObject("http://127.0.0.1:8081/api/http/testResponse", BaseResponse.class);
    }

    @GetMapping("testResponse")
    public String testResponse() {
        return "你好";
    }
}
