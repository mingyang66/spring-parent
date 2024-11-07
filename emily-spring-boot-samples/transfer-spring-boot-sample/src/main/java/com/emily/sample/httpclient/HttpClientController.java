package com.emily.sample.httpclient;

import com.emily.infrastructure.transfer.rest.annotation.TargetHttpTimeout;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.math.NumberUtils;
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


@RequestMapping("api/rest")
@RestController
public class HttpClientController {
    private final RestTemplate restTemplate;
    private final RestTemplateBuilder restTemplateBuilder;

    public HttpClientController(RestTemplate restTemplate, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplate;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @GetMapping("http")
    public BaseResponse get1(HttpServletRequest request) {
        BaseResponse result = restTemplate.getForObject("http://tradecenter.hafoo.app/query/api/spread/getFullCode", BaseResponse.class);
        return result;
    }

    @GetMapping("https")
    public BaseResponse https(HttpServletRequest request) {
        BaseResponse result = restTemplate.getForObject("https://tradecenter.hafoo.app/query/api/spread/getFullCode", BaseResponse.class);
        return result;
    }

    @GetMapping("getSleep")
    public String getSleep(HttpServletRequest request) {
        long timeout = Long.parseLong(request.getParameter("timeout"));
        String result = restTemplate.getForObject("http://127.0.0.1:8080/api/rest/sleep?timeout=" + timeout, String.class);
        return result;
    }

    @GetMapping("getTimeout")
    @TargetHttpTimeout(readTimeout = 6000, connectTimeout = 10000)
    public String getTimeout(HttpServletRequest request) {
        long timeout = Long.parseLong(request.getParameter("timeout"));
        String result = restTemplate.getForObject("http://127.0.0.1:8080/api/rest/sleep?timeout=" + timeout, String.class);
        return result;
    }

    @GetMapping("sleep")
    public String sleep(HttpServletRequest request) {
        long timeout = Long.parseLong(request.getParameter("timeout"));
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "timeout:" + timeout;
        }
        return "success";
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


}
