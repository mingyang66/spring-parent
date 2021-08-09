package com.emily.infrastructure.test;

import com.emily.infrastructure.common.utils.BeanUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.User;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) {
        Job job = new Job();
        job.setA("2332");
        job.setId(23L);
        job.setJobNumber(34L);
        job.setJobDesc("wererw");
        Map<String, Object> map = BeanUtils.beanToMapF(job);
        System.out.println(JSONUtils.toJSONPrettyString(map));

        User user = new User();
        user.username = "asdf";
        user.password="23";
        Map<String, Object> map1 = BeanUtils.beanToMapF(user);
        System.out.println(JSONUtils.toJSONPrettyString(map1));

        System.out.println(JSONUtils.toJSONPrettyString(getInParam(job)));
        System.out.println(JSONUtils.toJSONPrettyString(getInParam(user)));
    }

    public static  <T> Map<String, Object> getInParam(T t2Request) {
        Map<String, Object> params = Maps.newHashMap();
        try {
            //反射获取request属性，构造入参
            Class<?> classRequest = Class.forName(t2Request.getClass().getName());
            Field[] fields = classRequest.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].getName();
                Object fieldValue = fields[i].get(t2Request);

                params.put(fieldName, fieldValue);
            }
        } catch (Exception ex) {
        }
        return params;
    }
}
