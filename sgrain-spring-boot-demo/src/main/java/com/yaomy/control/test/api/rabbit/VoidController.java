package com.yaomy.control.test.api.rabbit;

import com.sgrain.boot.autoconfigure.returnvalue.annotation.ApiWrapperIgnore;
import com.yaomy.control.test.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * @program: spring-parent
 * @description: Void返回值类型测试
 * @create: 2020/03/03
 */
@ApiWrapperIgnore
@RestController
public class VoidController {
    @Autowired
    private Environment environment;

    @PostMapping(value = {"void/test1", "void/doubl"})
    public User test1(@RequestBody User user){
        System.out.println(environment.getProperty("test.a"));
        System.out.println(environment.getProperty("test.b"));
        System.out.println("-----test1----");
        user.setAge(12);
        return user;
    }

    @PostMapping("void/test2")
    public void test2(){
        System.out.println("-----test2----");
        throw new NullPointerException();
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
    public ResponseEntity<String> test6(){
        System.out.println("-----test5------");
        return ResponseEntity.ok("test5");
    }
}
