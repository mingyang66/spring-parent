package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.common.date.DatePatternType;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.entity.BaseResponseBuilder;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.DeSensitiveUtils;
import com.emily.infrastructure.test.mapper.mysql.MysqlMapper;
import com.emily.infrastructure.test.po.json.JsonRequest;
import com.emily.infrastructure.test.po.json.JsonResponse;
import com.emily.infrastructure.test.po.json.PubRequest;
import com.emily.infrastructure.test.po.json.PubResponse;
import com.emily.infrastructure.test.po.sensitive.MapperIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/10/27 10:52 上午
 */
@RestController
@RequestMapping("api/json")
public class SensitiveController {

    @PostMapping("test")
    @JsonSerialize
    public List<BaseResponse<JsonResponse>> test(@Validated @RequestBody List<JsonRequest> request) throws IllegalAccessException {
        List<JsonRequest> S = DeSensitiveUtils.acquire(request);
        System.out.println(JsonUtils.toJSONPrettyString(S));
        JsonResponse response = new JsonResponse();
        response.setPassword("123");
        response.setUsername("条消息");
        response.setEmail("1393619859@qq.com");
        response.setIdCard("321455188625645686");
        response.setBankCard("325648956125656666");
        response.setPhone("18254452658");
        response.setMobile("1234567");
        JsonResponse.Job job = new JsonResponse.Job();
        job.setEmail("1393619859@qq.com");
        job.setWork("你好");
        response.setJobs(new JsonResponse.Job[]{job, job});
        response.setList(Sets.newHashSet(job));
        response.setJob(job);
        response.setDateFormat(DatePatternType.YYYY_MM_DD_HH_MM_SS_SSS);
        String[] arr = new String[2];
        arr[0] = "test1";
        arr[1] = "test2";
        response.setArr(arr);
        List<BaseResponse<JsonResponse>> list = Lists.newArrayList(new BaseResponseBuilder<JsonResponse>().data(response).build());
        //return list;
        return DeSensitiveUtils.acquire(list);
    }

    @PostMapping("test1")
    public BaseResponse<PubResponse> test1(@Validated @RequestBody PubRequest request) throws IllegalAccessException {
        PubResponse response = new PubResponse();
        response.password = "32433";
        response.username = "条消息";
        response.email = "1393619859@qq.com";
        response.idCard = "321455188625645686";
        response.bankCard = "325648956125656666";
        response.phone = "18254452658";
        response.mobile = "1234567";
        PubResponse.Job job = new PubResponse.Job();
        job.email = "1393619859@qq.com";
        job.work = "呵呵哈哈哈";
        response.job = job;
        response.jobs = new PubResponse.Job[]{job};
        response.jobList = Arrays.asList(job);
        BaseResponse<PubResponse> r = new BaseResponseBuilder<PubResponse>().data(response).build();
        //return r;
        return DeSensitiveUtils.acquire(r);
    }

    @GetMapping("test3")
    public JsonResponse test3(@Validated @RequestBody JsonRequest request) {
        JsonResponse response = new JsonResponse();
        response.setPassword("123");
        response.setUsername("条消息");
        response.setEmail("1393619859@qq.com");
        response.setIdCard("321455188625645686");
        response.setBankCard("325648956125656666");
        response.setPhone("18254452658");
        response.setMobile("1234567");
        JsonResponse.Job job = new JsonResponse.Job();
        job.setEmail("1393619859@qq.com");
        job.setWork("你好");
        response.setJob(job);
        return response;
    }

    @GetMapping("test4")
    public String test4(String name, @RequestParam("phone") String phone, @RequestParam String username) {
        return phone + "-" + username;
    }

    @Autowired
    private MysqlMapper mysqlMapper;


    @GetMapping("testMapper")
    public MapperIgnore testMapper() {
        PubResponse response = new PubResponse();
        response.password = "32433";
        response.username = "条消息";
        response.email = "1393619859@qq.com";
        response.idCard = "321455188625645686";
        response.bankCard = "325648956125656666";
        response.phone = "18254452658";
        response.mobile = "1234567";
        PubResponse.Job job = new PubResponse.Job();
        job.email = "1393619859@qq.com";
        job.work = "呵呵哈哈哈";
        response.job = job;
        return mysqlMapper.getMapperIgnore(response, "江南七怪", "mingyangsky@foxmail.com");
    }
}
