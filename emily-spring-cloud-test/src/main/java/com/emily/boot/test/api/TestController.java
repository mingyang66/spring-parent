package com.emily.boot.test.api;

import com.emily.framework.common.utils.json.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/12/16
 */
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

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
    @PostMapping("test5")
    public void test5(@RequestParam Map<String, Object> params){
        System.out.println(JSONUtils.toJSONPrettyString(params));
        System.out.println(params);
    }
    @GetMapping("test6")
    public void test6(@RequestParam Map<String, Object> params){
        System.out.println(JSONUtils.toJSONPrettyString(params));
        System.out.println(params);
    }
}
