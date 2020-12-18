package com.emily.boot.test.api;

import com.netflix.niws.client.http.HttpClientLoadBalancerErrorHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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
    public String test(){
        return "success";
    }
    @GetMapping("test2")
    public String test2(){
        return "本服务";
    }
    @GetMapping("test3")
    public String test3(){
        String url = "http://172.30.67.122:8111/http/test1";
        String result = restTemplate.getForObject(url, String.class);
        String url1 = "http://172.30.67.122:8111/http/test5/{name}?pass=adsf&pass=123";
        String rest = restTemplate.getForObject(url1, String.class, "emliy");
        String url3 = "http://172.30.67.122:8111/http/test3/?name=好朋友&pass=你不知道";
        String rest3 = restTemplate.getForObject(url3, String.class);
        String rest4 = restTemplate1.getForObject("http://consul-demo/http/test3/?name=好朋友&pass=你不知道", String.class);
        return StringUtils.join(result, "--", rest, "--", rest3, "--", rest4);
    }
}
