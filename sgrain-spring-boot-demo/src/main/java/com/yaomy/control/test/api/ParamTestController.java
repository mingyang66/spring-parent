package com.yaomy.control.test.api;

import com.google.common.collect.Lists;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.yaomy.control.test.po.People;
import com.yaomy.control.test.po.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: spring-parent
 * @description: 参数单元测试
 * @author: 姚明洋
 * @create: 2020/05/20
 */
@RestController
public class ParamTestController {

    @PostMapping("api/param/test1")
    public void test1(@RequestBody User user, People people, @RequestParam String password, String name, HttpServletRequest request, HttpServletResponse response){
        System.out.println(JSONUtils.toJSONPrettyString(user));
    }
    @GetMapping("api/param/test2")
    public void test2(User user, People people, @RequestParam String password, String name, HttpServletRequest request, HttpServletResponse response){
        System.out.println(JSONUtils.toJSONPrettyString(user));
    }
    @PostMapping("api/param/test3")
    public void test3(MultipartFile file, String name){
        System.out.println("name:"+name);
        List<String> list = Lists.newArrayList();
        list.stream().findAny().orElse("1");
    }
}
