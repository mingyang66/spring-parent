package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.test.service.JobService;
import com.emily.infrastructure.test.service.JobServiceImpl;

import java.lang.reflect.Method;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */
public class A {
    public static void main(String[] args) throws NoSuchMethodException {
        JobService jobService = new JobServiceImpl();
        Method method = jobService.getClass().getMethod("findJob");
        System.out.println(method.isAnnotationPresent(TargetDataSource.class));
    }
}
