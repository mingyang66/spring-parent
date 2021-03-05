package com.emily.boot.test.api;

import com.emily.boot.test.api.po.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/12/16
 */
@RestController
public class TestController {

    @GetMapping("test1")
    public String test1(HttpServletResponse response){
        response.setContentType("text/html");
        return "success";
    }
    @GetMapping("test2")
    public String test2(String password){

        return "success-"+password;
    }
    @PostMapping("test3")
    public String test3(@RequestBody User user){
        return "success"+user.getName();
    }

    @PostMapping("test4")
    public String test4(@RequestBody User user){
        throw new RuntimeException("error");
    }

    @PostMapping("test5")
    public String test5(@RequestBody User user){
        try{
            Thread.sleep(3000);
        }catch (Exception e){

        }
        return "success";
    }

    @GetMapping("test6")
    public byte[] test6(){
        byte[] bytes = new byte[]{1,2};
        return bytes;
    }
    @GetMapping("test7")
    public byte test7(){
        return 2;
    }
}
