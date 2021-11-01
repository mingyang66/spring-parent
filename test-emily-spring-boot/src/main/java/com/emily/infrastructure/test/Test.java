package com.emily.infrastructure.test;

import com.emily.infrastructure.common.utils.bean.JavaBeanUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.test.po.Puser;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException {
        Duration duration = Duration.ofMillis(10000);
        System.out.println(duration.toMillis());
        System.out.println(duration.toMillisPart());
    }

}
