package com.emily.infrastructure.test.controller;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.emily.infrastructure.autoconfigure.httpclient.annotation.TargetHttpTimeout;
import com.emily.infrastructure.autoconfigure.httpclient.context.HttpContextHolder;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.test.mainTest.TestTimeout;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.client.config.RequestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    @GetMapping("get1")
    public BaseResponse get1(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result;
        try {
            HttpContextHolder.bind(RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(-1).build());
            result = restTemplate.getForObject("http://127.0.0.1:8080/api/http/testResponse?timeout=" + timeout, BaseResponse.class);
        } finally {
            HttpContextHolder.unbind();
        }
        return result;
    }

    @GetMapping("get2")
    @TargetHttpTimeout(readTimeout = 2000)
    public BaseResponse get2(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result = restTemplate.getForObject("http://127.0.0.1:8080/api/http/testResponse?timeout=" + timeout, BaseResponse.class);

        return result;
    }


    @GetMapping("testResponse")
    public String testResponse(HttpServletRequest request) throws IllegalArgumentException {
        String timeout = request.getParameter("timeout");
        try {
            Thread.sleep(NumberUtils.toLong(timeout, 0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "你好";
    }

    @Autowired
    private TestTimeout testTimeout;

    @PostConstruct
    public void init() {
        //获取环境变量，初始化服务器端IP
        ScheduledExecutorService service = TtlExecutors.getTtlScheduledExecutorService(Executors.newScheduledThreadPool(2));
        service.scheduleAtFixedRate(() -> {
            try {
                testTimeout.loadStr();
            } catch (Exception e) {
            }

        }, 5, 5, TimeUnit.SECONDS);
    }


}
