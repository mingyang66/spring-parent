package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.date.DatePatternType;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.test.po.json.JsonResponse;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */
public class A {
    public static void main(String[] args) {
        JsonResponse response = new JsonResponse();
        response.setA(1);
        response.setB(new byte[]{1, 2});
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
        int a = 1;
        SensitiveUtils.acquire(a);
        byte[] b = new byte[]{3, 4};
        SensitiveUtils.acquire(b);
        SensitiveUtils.acquire(response);
    }

    private static Object toObj(Object o, String... field) {
        if (Objects.isNull(o)) {
            return null;
        }
        if (o instanceof String) {
            String str = (String) o;
            if (StringUtils.isEmpty(str) || StringUtils.isBlank(str)) {
                return o;
            }
        }
        try {
            Map dataMap = JsonUtils.toJavaBean(JsonUtils.toJSONString(o), Map.class);
            Arrays.asList(field).stream().forEach(f -> {
                if (dataMap.containsKey(f)) {
                    dataMap.put(f, "----");
                }
            });
            return dataMap;
        } catch (Exception exception) {
            return o;
        }
    }
}
