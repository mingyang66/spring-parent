package com.emily.infrastructure.common.utils.bean;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description :  参数名工具类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/13 11:14 上午
 */
public class ParamNameUtils {
    /**
     * 获取参数名，按照实际参数顺序存储
     *
     * @param method
     * @return
     */
    public static List<String> getParamNames(Method method) {
        return getParameterNames(method);
    }

    /**
     * 获取构造函数的参数
     *
     * @param constructor
     * @return
     */
    public static List<String> getParamNames(Constructor<?> constructor) {
        return getParameterNames(constructor);
    }

    /**
     * 获取参数名
     * @param executable
     * @return
     */
    private static List<String> getParameterNames(Executable executable) {
        return Arrays.stream(executable.getParameters()).map(Parameter::getName).collect(Collectors.toList());
    }

    /**
     * 获取实际参数名
     *
     * @param method
     * @param paramIndex 参数索引，如：("0", "1", ...)
     * @return
     */
    public String getActualParamName(Method method, int paramIndex) {
        return getParamNames(method).get(paramIndex);
    }
}
