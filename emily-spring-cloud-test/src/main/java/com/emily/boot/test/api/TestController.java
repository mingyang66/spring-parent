package com.emily.boot.test.api;

import com.netflix.niws.client.http.HttpClientLoadBalancerErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/12/16
 */
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    @Qualifier("loadBalancer")
    private RestTemplate restTemplate1;

    @GetMapping("test")
    public String test() throws InterruptedException {
        Thread.sleep(200000);
        return "success";
    }
    @GetMapping("test2")
    public String test2() throws InterruptedException {
        return "本服务";
    }
    @PostMapping("test3")
    public String test3(@RequestParam String name, HttpServletResponse response) throws InterruptedException {
        System.out.println(name);
        response.setHeader("X-Response-Header", "you are Emily");
        response.setHeader("Location", "http://www.baidu.com/api/ttt");
        return "本服务";
    }
    @PostMapping("test4")
    public String test4(@RequestParam String name) throws InterruptedException {
        System.out.println(name);
        return "本服务";
    }
}
