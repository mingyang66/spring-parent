package com.yaomy.control.test.api.rabbit;

import com.sgrain.boot.common.utils.LoggerUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import com.sgrain.boot.context.httpclient.HttpClientService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: Void返回值类型测试
 * @create: 2020/03/03
 */
@RestController
public class TestController {

    @Autowired
    @Lazy
    private HttpClientService httpClientService;
    @Autowired
    @Lazy
    private AsyncConfigurer asyncConfigurer;
    @Autowired
    @Lazy
    private ThreadPoolTaskExecutor asyncTaskExecutor;

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
    public void client(){
        String url1 = "http://127.0.0.1:9000/api/http/test1";
        String url2 = "http://127.0.0.1:9000/api/http/test2/{name}";
        String url3 = "http://127.0.0.1:9000/api/http/test3?name=liming&pass=123456";
        String url4 = "http://127.0.0.1:9000/api/http/test4?name=lilei&pass=12345";
        String url5 = "http://127.0.0.1:9000/api/http/test5/{name}?pass=12345";
        String url6 = "http://127.0.0.1:9000/api/http/test6";
        String url7 = "http://127.0.0.1:9000/api/http/test7";
        String url8 = "http://127.0.0.1:9000/api/http/test8/{name}";
        String url9 = "http://127.0.0.1:9000/api/http/test9/{name}";


        String result1 = httpClientService.get(url1, String.class);
        String result2 = httpClientService.get(url2, String.class, "liming");
        String result3 = httpClientService.get(url3, String.class);
        String result4 = httpClientService.get(url4, String.class);
        String result5 = httpClientService.get(url5, String.class, "lisiyuan");
        MultiValueMap<String, Object> map6 = new LinkedMultiValueMap<>();
        map6.add("name", "lili");
        map6.add("pass", "666");
        String result6 = httpClientService.post(url6, map6,null, String.class);
        Map<String, Object> map7 = new HashMap<>();
        map7.put("name", "lili");
        map7.put("age", 7);
        String result7 = httpClientService.post(url7, map7,null, String.class);

        Map<String, Object> map8 = new HashMap<>();
        map8.put("name", "lili8");
        map8.put("age", 8);
        String result8 = httpClientService.post(url8, map8,null, String.class, "li88");

        MultiValueMap<String, String> map9 = new LinkedMultiValueMap<>();
        map9.add("length", "lili9");
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("token", UUIDUtils.randomUUID());
        Map<String, Object> result9 = httpClientService.post(url9, map9,headers, Map.class, "li88");

        String url10 = "http://10.10.86.229:8108/api/fund/card/getFundAggregateDetailsPage?isinCode=HK0000323219&pageSize=7";
        String result10 = httpClientService.get(url10, String.class);
    }
    @PostMapping("client1")
    public void client1(@RequestParam MultipartFile file, @RequestParam String name){

        String url9 = "http://127.0.0.1:9000/api/http/test9/{name}";
        MultiValueMap<String, String> map9 = new LinkedMultiValueMap<>();
        map9.add("length", "lili9");
        Map<String, Object> result9 = httpClientService.post(url9, map9,null, Map.class, "li88");
        System.out.println(result9);
    }
    @GetMapping("client2")
    public String client2(){
        //System.out.println(taskExecutor.getThreadNamePrefix());
        System.out.println(asyncTaskExecutor.getThreadNamePrefix());
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor)asyncConfigurer.getAsyncExecutor();
        return StringUtils.join("CorePoolSize：", taskExecutor.getCorePoolSize()+", 最大线程数："+taskExecutor.getMaxPoolSize()+"，当前线程池大小："+taskExecutor.getPoolSize(), ",当前活跃线程数：", taskExecutor.getActiveCount());
    }
}
