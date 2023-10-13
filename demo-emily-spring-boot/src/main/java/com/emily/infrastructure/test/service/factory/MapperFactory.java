package com.emily.infrastructure.test.service.factory;

import com.emily.infrastructure.core.exception.BusinessException;
import com.emily.infrastructure.core.exception.HttpStatusType;
import com.emily.infrastructure.core.context.ioc.IocUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * 获取Mapper实例对象帮助类
 * @author  Emily
 * @since  Created in 2022/3/24 4:23 下午
 */
public class MapperFactory<T> {
    /**
     * 判定条件参数
     */
    private String param;
    /**
     * 默认Oracle Mapper对象
     */
    private Class<T> defaultClass;
    /**
     * Mysql Mapper对象
     */
    private Class<? extends T> targetClass;

    public MapperFactory(Class<T> defaultClass, Class<? extends T> targetClass) {
        this.defaultClass = defaultClass;
        this.targetClass = targetClass;
    }

    public String getParam() {
        return param;
    }

    public Class<T> getDefaultClass() {
        return defaultClass;
    }

    public Class<? extends T> getTargetClass() {
        return targetClass;
    }

    public T getMapper() {
        this.param = null;
        return getBean(null, defaultClass, targetClass);
    }

    public T getMapper(String param) {
        this.param = param;
        return getBean(param, defaultClass, targetClass);
    }

    /**
     * 获取工厂实例对象
     *
     * @param defaultClass 默认Oracle Mapper对象
     * @return
     */
    public static MapperFactory newInstance(Class<?> defaultClass) {
        return newInstance(defaultClass, null);
    }

    /**
     * 获取工厂实例对象
     *
     * @param defaultClass 默认Oracle Mapper对象
     * @param targetClass  目标Mysql Mapper对象
     * @return
     */
    public static MapperFactory newInstance(Class<?> defaultClass, Class<?> targetClass) {
        return new MapperFactory(defaultClass, targetClass);
    }

    /**
     * 获取Mapper实例对象
     *
     * @param param        判定条件参数
     * @param defaultClass 默认oracle数据库Mapper
     * @param targetClass  Mysql数据库Mapper
     * @param <T>
     * @return
     */
    private <T> T getBean(String param, Class<T> defaultClass, Class<? extends T> targetClass) {
        //获取Mapper实例对象名
        String beanName = defaultClass.getSimpleName();
        //获取实例对象对应的所有bean集合
        Map<String, T> beanMaps = IocUtils.getBeansOfType(defaultClass);
        if (!beanMaps.containsKey(beanName)) {
            throw new BusinessException(HttpStatusType.ILLEGAL_ACCESS);
        }
        //todo 符合条件
        if (StringUtils.isEmpty(param) || isOracle(param)) {
            return beanMaps.get(beanName);
        }
        beanName = targetClass.getSimpleName();
        if (!beanMaps.containsKey(beanName)) {
            throw new BusinessException(HttpStatusType.ILLEGAL_ACCESS);
        }
        return beanMaps.get(beanName);
    }

    /**
     * 判定是否是oracle数据库
     *
     * @param param
     * @return
     */
    private static boolean isOracle(String param) {
        if (StringUtils.equals(param, "1")) {
            return true;
        }
        return false;
    }
}
