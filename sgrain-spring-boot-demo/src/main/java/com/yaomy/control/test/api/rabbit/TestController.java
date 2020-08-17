package com.yaomy.control.test.api.rabbit;

import com.sgrain.boot.common.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @program: spring-parent
 * @description: Void返回值类型测试
 * @create: 2020/03/03
 */
@RestController
public class TestController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("void/test46/{name}")
    public String test2(@PathVariable String name){
        for(int i=0;i<1;i++){
            LoggerUtils.module(TestController.class, "EMIS-TEST", "EMIS"+i+"你好-------TestController---------哈哈哈---------");
            LoggerUtils.info(TestController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"info");
            LoggerUtils.debug(TestController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"debug");
            LoggerUtils.error(TestController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"error");
            LoggerUtils.warn(TestController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"warn");
            LoggerUtils.trace(TestController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"trace");
        }
        return "success"+name;
    }

    @GetMapping("client")
    public ResponseEntity<String> client(){
        String url = "http://127.0.0.1:9000/api/void/test2";
        return restTemplate.getForEntity(url, String.class);
    }
}
