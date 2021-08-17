package com.emily.infrastructure.test;

import com.emily.infrastructure.common.utils.bean.BeanUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.Puser;
import com.emily.infrastructure.test.po.User;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        Map<String, Object> map = new HashMap<>();
        map.put("username", "namd");
        map.put("age", "12");



        System.out.println(JSONUtils.toJSONPrettyString(BeanUtils.mapToBeanAntiPattern(map, Puser.class)));
    }

}
