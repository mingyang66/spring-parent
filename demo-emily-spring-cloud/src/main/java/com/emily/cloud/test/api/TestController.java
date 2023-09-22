package com.emily.cloud.test.api;

import com.emily.cloud.test.api.po.User;
import com.emily.cloud.test.api.po.ValidateCodeUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Emily
 * @program: spring-parent
 *
 * @since 2020/12/16
 */
@RestController
@RequestMapping("api/test")
public class TestController {

    @Autowired
    private Environment environment;

    @GetMapping("test1")
    public String test1(HttpServletResponse response) {
        response.setContentType("text/html");
        return environment.getProperty("c");
    }

    @PostMapping("test2")
    public String test2(@Validated @RequestBody User user) {
        return "success-" + user.getName();
    }

    @PostMapping("test3")
    public String test3(@RequestBody User user) {
        return "success";
    }

    @PostMapping("test4")
    public String test4(@RequestBody User user) {
        throw new RuntimeException("error");
    }

    @PostMapping("test5")
    public String test5(@RequestBody User user) {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {

        }
        return "success";
    }

    @GetMapping("test6")
    public byte[] test6() {
        byte[] bytes = new byte[]{1, 2};
        return bytes;
    }

    @GetMapping("test7")
    public String test7() {
        String message = "你好，我是李焕英，哈哈哈哈哈哈哈哈哈哈或或或或或或或或或或或或或";
        String s = "";
        for (int i = 0; i < 10000; i++) {
            s = StringUtils.join(s, message);
        }
        return s;
    }

    @GetMapping("/gateway/createImage")
    public Map.Entry<String, Object> imageCode() {
        return ValidateCodeUtils.createImage(6);
    }

    @GetMapping("status")
    public ResponseEntity<String> status() {
        return new ResponseEntity<>("abce", HttpStatus.BAD_GATEWAY);
    }
}
