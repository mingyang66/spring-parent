package com.emily.sample.redis.controller;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.redis.common.RedisKeyspace;
import com.emily.infrastructure.redis.factory.DataRedisFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.types.RedisClientInfo;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


@RestController
public class RedisController {
    private final StringRedisTemplate stringRedisTemplate;
    private final StringRedisTemplate testStringRedisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;
    private final RedisTemplate<Object, Object> testRedisTemplate;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final ReactiveStringRedisTemplate testReactiveStringRedisTemplate;
    private final ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, Object> testReactiveRedisTemplate;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public RedisController(StringRedisTemplate stringRedisTemplate,
                           @Qualifier("test1StringRedisTemplate") StringRedisTemplate testStringRedisTemplate,
                           RedisTemplate<Object, Object> redisTemplate,
                           @Qualifier("test1RedisTemplate") RedisTemplate<Object, Object> testRedisTemplate,
                           ReactiveStringRedisTemplate reactiveStringRedisTemplate,
                           @Qualifier("test1ReactiveStringRedisTemplate") ReactiveStringRedisTemplate testReactiveStringRedisTemplate,
                           ReactiveRedisTemplate<String, Object> reactiveRedisTemplate,
                           @Qualifier("test1ReactiveRedisTemplate") ReactiveRedisTemplate<String, Object> testReactiveRedisTemplate
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.testStringRedisTemplate = testStringRedisTemplate;
        this.redisTemplate = redisTemplate;
        this.testRedisTemplate = testRedisTemplate;
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
        this.testReactiveStringRedisTemplate = testReactiveStringRedisTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.testReactiveRedisTemplate = testReactiveRedisTemplate;
    }

    @GetMapping("api/redis/test")
    public String test() {

        DataRedisFactory.getStringRedisTemplate().opsForValue().set("test", "你好", 100, TimeUnit.SECONDS);
        DataRedisFactory.getStringRedisTemplate("test1").opsForValue().set("test", "你好", 100, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set("test1", "你好1", 100, TimeUnit.SECONDS);
        testStringRedisTemplate.opsForValue().set("test2", "你好2", 100, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set("test3", "test3", 100, TimeUnit.SECONDS);
        testRedisTemplate.opsForValue().set("test3", "test3", 100, TimeUnit.SECONDS);
        return DataRedisFactory.getStringRedisTemplate().opsForValue().get("test");
    }

    @GetMapping("api/redis/reactive")
    public String getInfo() {
        DataRedisFactory.getReactiveStringRedisTemplate().opsForValue().set("test", "你好", Duration.ofSeconds(100)).block();
        DataRedisFactory.getReactiveStringRedisTemplate("test1").opsForValue().set("test", "你好", Duration.ofSeconds(100)).block();
        reactiveStringRedisTemplate.opsForValue().set("test1", "你好1", Duration.ofSeconds(100)).block();
        testReactiveStringRedisTemplate.opsForValue().set("test1", "你好2", Duration.ofSeconds(100)).block();
        reactiveRedisTemplate.opsForValue().set("test3", "test3", Duration.ofSeconds(100)).block();
        testReactiveRedisTemplate.opsForValue().set("test4", "test4", Duration.ofSeconds(100)).block();
        reactiveRedisTemplate.opsForHash().put("test5","test5","test5").block();
        reactiveRedisTemplate.opsForHash().put("test5","test6","test6").block();
        return DataRedisFactory.getReactiveStringRedisTemplate().opsForValue().get("test").subscribe().toString();
    }

    @GetMapping("getTest")
    public String getTest() {

        DataRedisFactory.getStringRedisTemplate().opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
        DataRedisFactory.getStringRedisTemplate().opsForValue().set("test66", "123", 12);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("te", 12);
        dataMap.put("te2", 12);
        dataMap.put("te3", "哈哈");
        DataRedisFactory.getRedisTemplate().opsForValue().set("test1", dataMap, 1, TimeUnit.MINUTES);
        return DataRedisFactory.getStringRedisTemplate().opsForValue().get("test") + "-" +
                JsonUtils.toJSONPrettyString(DataRedisFactory.getRedisTemplate().opsForValue().get("test1"));
    }

    @GetMapping("getTest1")
    public String getTest1() {

        DataRedisFactory.getStringRedisTemplate("test1").opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
        DataRedisFactory.getStringRedisTemplate("test1").opsForValue().set("test66", "123", 12);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("te", 122);
        dataMap.put("te2", 12333);
        dataMap.put("te3", "哈哈66666");
        DataRedisFactory.getRedisTemplate("test1").opsForValue().set("test1", dataMap, 1, TimeUnit.MINUTES);
        return DataRedisFactory.getStringRedisTemplate("test1").opsForValue().get("test") + "-" +
                JsonUtils.toJSONPrettyString(DataRedisFactory.getRedisTemplate("test1").opsForValue().get("test1"));
    }

    @GetMapping("getCn")
    public Object get2() {
        for (int i = 0; i < 1000; i++) {
            threadPoolTaskExecutor.execute(() -> {
                DataRedisFactory.getRedisTemplate("test").opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
            });
            threadPoolTaskExecutor.execute(() -> {
                DataRedisFactory.getRedisTemplate("test1").opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
            });
            threadPoolTaskExecutor.execute(() -> {
                DataRedisFactory.getStringRedisTemplate("test").opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
            });
            threadPoolTaskExecutor.execute(() -> {
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("te", 12);
                dataMap.put("te2", 12);
                dataMap.put("te3", "年好吗");
                DataRedisFactory.getRedisTemplate("test").opsForValue().set("test1", dataMap, 1, TimeUnit.MINUTES);
            });
            threadPoolTaskExecutor.execute(() -> {
                DataRedisFactory.getStringRedisTemplate("test1").opsForValue().set("test", "123", 12, TimeUnit.MINUTES);
            });

        }
        //RedisDbFactory.getRedisTemplate("one").opsForValue().set("one", dataMap, 1, TimeUnit.MINUTES);
        return DataRedisFactory.getRedisTemplate("test").opsForValue().get("test1");
    }

    @GetMapping("pool")
    public String pool() {
        List<RedisClientInfo> list = DataRedisFactory.getRedisTemplate().getClientList();
        System.out.println(list.size());
        list.stream().forEach(redisClientInfo -> {
            System.out.println(JsonUtils.toJSONPrettyString(redisClientInfo));
        });
        return "success";
    }

    @GetMapping("get")
    public String indicator() {
        return DataRedisFactory.getStringRedisTemplate().opsForValue().get("test");
    }


    @GetMapping("hash")
    public void hash(@RequestParam("code") String code) {
        String key = RedisKeyspace.of("EMIS-TEST", code);
        StringRedisTemplate stringRedisTemplate = DataRedisFactory.getStringRedisTemplate();
        stringRedisTemplate.opsForHash().put(key, "accountCode" + code, code);
    }

    @GetMapping("lock")
    public Boolean lock(@RequestParam("code") String code) {
        String key = RedisKeyspace.of("Emily-Test", "123");
        StringRedisTemplate stringRedisTemplate = DataRedisFactory.getStringRedisTemplate();
        return stringRedisTemplate.opsForValue().setIfAbsent(key, code, 10, TimeUnit.SECONDS);
    }

    @GetMapping("setBit")
    public Long setBit(@RequestParam("accountCode") Long accountCode, @RequestParam("date") String date, @RequestParam("offset") Long offset) {
        String key = RedisKeyspace.of("Emily-Test", "bloom", date, accountCode + "");
        StringRedisTemplate stringRedisTemplate = DataRedisFactory.getStringRedisTemplate();
        Boolean flag = stringRedisTemplate.opsForValue().setBit(key, offset, true);
        System.out.println(flag);
        return stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount(key.getBytes()));
    }

    @GetMapping("getBit")
    public Boolean getBit(@RequestParam("accountCode") Long accountCode, @RequestParam("date") String date, @RequestParam("offset") Long offset) {
        String key = RedisKeyspace.of("Emily-Test", "bloom", date, accountCode + "");
        StringRedisTemplate stringRedisTemplate = DataRedisFactory.getStringRedisTemplate();
        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.getBit(key.getBytes(), offset));
    }

    @GetMapping("batch")
    public void batchBit(@RequestParam("accountCode") Long accountCode) {
        String key = RedisKeyspace.of("Emily-Test", "bloom", accountCode + "");
        StringRedisTemplate stringRedisTemplate = DataRedisFactory.getStringRedisTemplate();
        for (int i = 0; i < 10000; i++) {
            // stringRedisTemplate.opsForValue().bitField(key, new BitFieldSubCommands(sd));
        }
    }

    @GetMapping("expire")
    public void expire() {
        stringRedisTemplate.opsForValue().set("test_expire", "test", 59, TimeUnit.SECONDS);
        Long expire = stringRedisTemplate.getExpire("test_expire");
        System.out.println(expire);
        Long expire1 = stringRedisTemplate.getExpire("test_expire", TimeUnit.MINUTES);
        System.out.println(expire1);
        // todo 过期时间不可为0，为0则抛出异常
        stringRedisTemplate.opsForValue().set("test_expire", "test", expire1, TimeUnit.MINUTES);
    }
}
