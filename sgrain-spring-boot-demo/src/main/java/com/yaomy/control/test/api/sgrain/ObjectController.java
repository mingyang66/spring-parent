package com.yaomy.control.test.api.sgrain;

import com.google.common.collect.Lists;
import com.sgrain.boot.common.utils.json.JSONUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 对象测试类
 * @author: 姚明洋
 * @create: 2020/05/22
 */
@Api(tags = "对象控制器")
@RestController
public class ObjectController {

    @GetMapping("obj/test1")
    public void test1(){
        List names = new ArrayList();

        names.add("Google");
        names.add("Runoob");
        names.add("Taobao");
        names.add("Baidu");
        names.add("Sina");

        names.forEach(System.out::println);
        names.forEach(JSONUtils::toJSONString);
    }
    public static void main(String args[]) {
        final int num = 1;
        Converter<Integer, String> s = (param) -> System.out.println(String.valueOf(param + num));
        s.convert(2);  // 输出结果为 3
    }

    public interface Converter<T1, T2> {
        void convert(int i);
    }
    @Autowired
    private ApplicationContext applicationContext;

    @RequestMapping(value = {
            "path/getAllUrl",
            "path/getAllUrl1"
    }, method= {
            RequestMethod.GET,
            RequestMethod.POST
    }, headers = {
            "auth=123"
    }
    )
    public List<String> getAllUrl(@RequestParam String name) throws Exception {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map =mapping.getHandlerMethods();
       // HandlerExecutionChain chain = mapping.getHandler(RequestUtils.getRequest());
        List<String> urls = Lists.newArrayList();
     /*   for(RequestMappingInfo info:map.keySet()){
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            for(String url:patterns){
                urls.add(url);
            }
        }*/
        return urls;
    }
    @Autowired(required = false)
    public void setConfigurers(List<WebMvcConfigurer> configurers) {
        if (!CollectionUtils.isEmpty(configurers)) {
        }
    }
}
