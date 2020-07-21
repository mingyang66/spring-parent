package com.yaomy.control.test.api.sgrain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sgrain.boot.autoconfigure.aop.annotation.RateLimit;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.yaomy.control.test.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: Redis控制器
 * @create: 2020/03/19
 */
@RestController
public class RedisController {
    @Autowired
    @Lazy
    private RedisTemplate redisTemplate;

    @GetMapping("/redis/test")
    @RateLimit(permits = 10, timeUnit = TimeUnit.SECONDS)
    public String testRedisson(){
       // redisTemplate.opsForValue().set("test", "测试数据abc123");

        return "SUCCESS";
    }
    @GetMapping("/redis/test1")
    public String testRedisson1(){
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String, Object> map = Maps.newHashMap();
        map.put("a", 23);
        map.put("b", "asfd");
        list.add(map);
        User user = new User();
        user.setName("fsd");
        user.setAge(12);
        user.setDate(new Date());
        user.setList(list);
        user.setWeight(new String[]{"12","23"});
        //redisTemplate.opsForValue().set("test", user);

        User user1 = (User) redisTemplate.opsForValue().get("test");
        System.out.println(JSONUtils.toJSONString(user1));

        return "SUCCESS";
    }
}
