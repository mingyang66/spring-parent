package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: spring-parent
 * @description: 参数控制器
 * @author: Emily
 * @create: 2021/10/30
 */
@RestController
@RequestMapping("api/param")
public class ParamController {
    @GetMapping("test")
    public String test() {
        return "afdss";
    }

    @PostMapping("test1")
    public Job test1(@Validated @RequestBody Job job) {
        return job;
    }

    @PostMapping("postList")
    public int postList(@RequestBody List<User> list) {
        System.out.println(JSONUtils.toJSONPrettyString(list));
        return 0;
    }

    @PostMapping("postArray")
    public int postList(@RequestBody User[] list) {
        System.out.println(JSONUtils.toJSONPrettyString(list));
        return 0;
    }

    @GetMapping("rest/{code}/{name}")
    public String restTest(@PathVariable("code") String code, @PathVariable("name") String name) {
        return "s";
    }
}
