package com.emily.infrastructure.common.utils;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.google.common.collect.Maps;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: bean相互转换工具类
 * @author: Emily
 * @create: 2021/05/28
 */
public class BeanUtils {
    /**
     * 将bean转换为Map
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        if (bean == null) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> map = Maps.newHashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                // 过滤class属性
                if (!key.equals("class")) {
                    // 得到property对应的getter方法
                    Method getter = property.getReadMethod();
                    // 获取属性方法值
                    Object value = getter.invoke(bean);
                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            throw new BusinessException(AppHttpStatus.CLASS_CAST_EXCEPTION.getStatus(), "类型转换异常");
        }
    }

    /**
     * 将Map数据转换为Bean
     *
     * @param type bean
     * @param map  数据集合
     * @param <T>  类类型
     * @return
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> type) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            T bean = type.getDeclaredConstructor().newInstance();
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                if (map.containsKey(propertyName)) {
                    Object value = map.get(propertyName);
                    descriptor.getWriteMethod().invoke(bean, value);
                }
            }
            return bean;
        } catch (Exception e) {
            throw new BusinessException(AppHttpStatus.CLASS_CAST_EXCEPTION.getStatus(), "Map转Bean异常");
        }
    }
}
