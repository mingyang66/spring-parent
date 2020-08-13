package com.yaomy.control.test.api.rabbit;

import com.sgrain.boot.common.utils.LoggerUtils;
import com.yaomy.control.test.po.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.UUID;

/**
 * @program: spring-parent
 * @description: Void返回值类型测试
 * @create: 2020/03/03
 */
@RestController
public class VoidController {
    @Autowired
    private Environment environment;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping(value = {"void/test1", "void/doubl"})
    public User test1(@RequestBody User user){
        System.out.println(environment.getProperty("test.a"));
        System.out.println(environment.getProperty("test.b"));
        System.out.println("-----test1----");
        user.setAge(12);
        return user;
    }
    @GetMapping("void/test2")
    public String test2(){
        BoundValueOperations operations = stringRedisTemplate.boundValueOps("test:tt");
        for(int i=0;i<10;i++) {
            operations.set("hhhhhhhhhhhhhhhh"+i);
            LoggerUtils.module(VoidController.class, "EMIS-VOID", "EMIS" + i + "你好---------VoidController-------哈哈哈---------");
            LoggerUtils.info(VoidController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"info");
            LoggerUtils.debug(VoidController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"debug");
            LoggerUtils.error(VoidController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"error");
            LoggerUtils.warn(VoidController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"warn");
            LoggerUtils.trace(VoidController.class, "EMIS"+i+"你好----------------哈哈哈---------"+"trace");
        }
        return "success";
    }

    @PostMapping("void/test3")
    public ResponseEntity test3(ArrayList<String> list){
        System.out.println("-----test3----");
        return ResponseEntity.ok().build();
    }
    @GetMapping("void/test4")
    public ResponseEntity<String> test4(){
        System.out.println("-----test4----");
        return ResponseEntity.ok("sadfsdf");
    }
    @GetMapping("void/test5")
    public String test5(){
        System.out.println("-----test5------");
        return "/actuator/health";
    }

    @GetMapping("void/test6")
    public String test6(HttpServletRequest request){
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()){
            String e = enumeration.nextElement();
            System.out.println(e+"=="+request.getHeader(e));
        }
        System.out.println("-----test5------");
        String s = "";
        for(int i=0;i<2048;i++){
            s = StringUtils.join(s, UUID.randomUUID().toString(), "-");
        }
        return s;
    }

}
