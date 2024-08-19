package com.emily.infrastructure.test.controller;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import com.emily.infrastructure.test.test.TestTimeout;
import com.emily.infrastructure.transfer.httpclient.annotation.TargetHttpTimeout;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Emily
 * @program: spring-parent
 * http控制器
 * @since 2021/11/11
 */
@RequestMapping("api/http")
@RestController
public class HttpClientController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @GetMapping("get1")
    public BaseResponse get1(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        BaseResponse<String> result;
        try {
            //   HttpContextHolder.bind(RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(-1).build());
            result = restTemplate.getForObject("http://127.0.0.1:8080/api/http/testResponse?timeout=" + timeout, BaseResponse.class);
        } finally {
            //  HttpContextHolder.unbind();
        }
        return result;
    }

    @GetMapping("get2")
    @TargetHttpTimeout(readTimeout = 2000, connectTimeout = -1)
    public String get2(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        String result = restTemplate.getForObject("https://127.0.0.1:8080/api/http/testResponse?timeout=1000", String.class);

        return result;
    }


    @RequestMapping(value = "testResponse")
    public String testResponse(HttpServletRequest request) {
        String timeout = request.getParameter("timeout");
        try {
            Thread.sleep(NumberUtils.toLong(timeout, 0));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, String> dataMap = Maps.newHashMap();
        dataMap.put("param1", "value1");
        return "你好";
    }

    @GetMapping("testHeader")
    public BaseResponse testHeader() {
        String url = "http://127.0.0.1:8080/api/http/testResponse";
        HttpHeaders headers = new HttpHeaders();
        headers.set("username", "田晓霞");
        headers.set("password", "平凡的世界");

        Map<String, String> body = Maps.newHashMap();
        body.put("param1", "value1");
        body.put("param2", "value2");
        // ResponseEntity<BaseResponse> entity = new RestTemplateBuilder().build().postForEntity(url, new HttpEntity<>(body, headers), BaseResponse.class);
        ResponseEntity<BaseResponse> entity = new RestTemplateBuilder().build().exchange(url, HttpMethod.GET, new HttpEntity<>(body, headers), BaseResponse.class);
        return entity.getBody();
    }

    @Autowired
    private TestTimeout testTimeout;

    @PostConstruct
    public void init() {
        //获取环境变量，初始化服务器端IP
        ScheduledExecutorService service = TtlExecutors.getTtlScheduledExecutorService(Executors.newScheduledThreadPool(2));
       /* service.scheduleAtFixedRate(() -> {
            try {
                // testTimeout.loadStr();
            } catch (Exception e) {
            }

        }, 5, 5, TimeUnit.SECONDS);*/
    }


}
