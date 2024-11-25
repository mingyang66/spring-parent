package com.emily.sample.transfer.rest;

import com.emily.infrastructure.transfer.rest.annotation.TargetHttpTimeout;
import com.emily.sample.transfer.entity.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RequestMapping("api/rest")
@RestController
public class HttpClientController {
    private final RestTemplate restTemplate;

    public HttpClientController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("http")
    public BaseResponse http(HttpServletRequest request) {
        BaseResponse result = restTemplate.getForObject("http://tradecenter.hafoo.work/query/api/spread/getFullCode", BaseResponse.class);
        return result;
    }

    @GetMapping("https")
    public BaseResponse https(HttpServletRequest request) {
        BaseResponse result = restTemplate.getForObject("https://tradecenter.hafoo.work/query/api/spread/getFullCode", BaseResponse.class);
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
}
