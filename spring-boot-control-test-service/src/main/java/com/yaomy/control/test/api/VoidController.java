package com.yaomy.control.test.api;

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
@CrossOrigin("http://www.bubuko.com/")
@RestController
public class VoidController {
    @Autowired
    private Environment environment;

    @PostMapping("void/test1")
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

    @PostMapping("void/test4")
    public ResponseEntity<Void> test4(ArrayList<String> list){
        System.out.println("-----test4----");
        return ResponseEntity.ok(null);
    }

    @PostMapping("void/test5")
    public ResponseEntity<String> test5(){
        System.out.println("-----test5------");
        return ResponseEntity.ok("test5");
    }

    @GetMapping("void/test6")
    public ResponseEntity<String> test6(){
        System.out.println("-----test5------");
        return ResponseEntity.ok("test5");
    }
}
