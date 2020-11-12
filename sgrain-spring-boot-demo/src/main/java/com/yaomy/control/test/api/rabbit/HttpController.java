package com.yaomy.control.test.api.rabbit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sgrain.boot.common.utils.RequestUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.sgrain.boot.context.api.model.UrlMappingInfo;
import com.yaomy.control.test.po.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/18
 */
@RestController
@RequestMapping("http")
public class HttpController {
    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @GetMapping("requestMappingInfo")
    public List<UrlMappingInfo> requestMappingInfo(){
        List<UrlMappingInfo> result = Lists.newArrayList();
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        map.forEach((info, method)->{
            UrlMappingInfo urlMappingInfo = new UrlMappingInfo();
            PatternsRequestCondition patternsRequestCondition = info.getPatternsCondition();
            urlMappingInfo.setMethod(info.getMethodsCondition().getMethods());
            urlMappingInfo.setPatterns(patternsRequestCondition.getPatterns());
            urlMappingInfo.setBean(method.getBean());
            urlMappingInfo.setDescription(method.toString());
            result.add(urlMappingInfo);
        });
        return result;
    }

    @GetMapping("test1")
    public String test1(HttpServletRequest request) throws InterruptedException {
        System.out.println(request.getProtocol());
        Thread.sleep(5500);
        System.out.println("---------------test1-------------");
        return "测试编码格式";
    }

    @GetMapping("test2/{name}")
    public String test2(@PathVariable String name) {
        return name+ "---"+ RequestUtils.getRequest().getContentType();
    }

    @GetMapping("test3")
    public String test3(String name, String pass) {
        return StringUtils.join(name, pass, "---", RequestUtils.getRequest().getContentType());
    }

    @GetMapping("test4")
    public String test4(HttpServletRequest request) {
        String name = request.getParameter("name");
        String pass = request.getParameter("pass");
        return StringUtils.join(name, pass, "---", RequestUtils.getRequest().getContentType());
    }

    @GetMapping("test5/{name}")
    public List<Map<String, Object>> test5(@PathVariable String name, @RequestParam String pass) {
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", "lisiyuan");
        map.put("age", 12);
        map.put("weight", null);
        list.add(map);
        return list;
    }

    @PostMapping("test6")
    public String test6(@RequestParam String name, @RequestParam String pass) {
        return StringUtils.join(name, pass, "---", RequestUtils.getRequest().getContentType());
    }

    @PostMapping("test7")
    public String test7(@RequestBody User user) {
        return StringUtils.join(user.getName(), user.getAge(), "---", RequestUtils.getRequest().getContentType());
    }
    @PostMapping("test8/{name}")
    public String test8(@RequestBody User user, @PathVariable String name, HttpServletRequest request) {
        return StringUtils.join(user.getName(), user.getAge(), name, "---", RequestUtils.getRequest().getContentType());
    }
    @PostMapping("test9/{name}")
    public String test9(@PathVariable String name, @RequestParam(required = true) String length, @RequestHeader String token) {
        return StringUtils.join(name, length, "---", RequestUtils.getRequest().getContentType());
    }
}
