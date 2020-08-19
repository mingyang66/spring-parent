package com.yaomy.control.test.api.rabbit;

import com.sgrain.boot.autoconfigure.returnvalue.annotation.ApiWrapperIgnore;
import com.yaomy.control.test.po.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/08/18
 */
@RestController
@RequestMapping("http")
public class HttpController {

    @ApiWrapperIgnore
    @GetMapping("test1")
    public String test1() {
        return "success";
    }

    @ApiWrapperIgnore
    @GetMapping("test2/{name}")
    public String test2(@PathVariable String name) {
        return name;
    }

    @ApiWrapperIgnore
    @GetMapping("test3")
    public String test3(String name, String pass) {
        return StringUtils.join(name, pass);
    }

    @ApiWrapperIgnore
    @GetMapping("test4")
    public String test4(HttpServletRequest request) {
        String name = request.getParameter("name");
        String pass = request.getParameter("pass");
        return StringUtils.join(name, pass);
    }

    @ApiWrapperIgnore
    @GetMapping("test5/{name}")
    public String test5(@PathVariable String name, String pass) {
        return StringUtils.join(name, pass);
    }

    @PostMapping("test6")
    public String test6(@RequestParam String name, @RequestParam String pass) {
        return StringUtils.join(name, pass);
    }

    @PostMapping("test7")
    public String test7(@RequestBody User user) {
        return StringUtils.join(user.getName(), user.getAge());
    }
    @PostMapping("test8/{name}")
    public String test8(@RequestBody User user, @PathVariable String name, HttpServletRequest request) {
        return StringUtils.join(user.getName(), user.getAge(), name);
    }
    @PostMapping("test9/{name}")
    public String test8(@PathVariable String name, @RequestParam String length) {
        return StringUtils.join(name, length);
    }
}
