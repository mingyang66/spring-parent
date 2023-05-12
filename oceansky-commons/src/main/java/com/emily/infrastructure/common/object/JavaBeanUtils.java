package com.emily.infrastructure.common.object;

import com.google.common.collect.Maps;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spring-parent
 * @description: bean相互转换工具类
 * @author: Emily
 * @create: 2021/05/28
 */
public class JavaBeanUtils {
    /**
     * 将bean转换为Map
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> copyToMap(T bean) {
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
        }
        return null;
    }

    /**
     * 反模式类型转换，即所有的属性必须为public修饰
     *
     * @param bean
     * @param <T>
     * @return
     */
    public static <T> Map<String, Object> copyToMapAntiPattern(T bean) {
        if (bean == null) {
            return Collections.emptyMap();
        }
        try {
            Map<String, Object> map = Maps.newHashMap();
            //反射获取request属性，构造入参
            Class<?> classRequest = Class.forName(bean.getClass().getName());
            Field[] fields = classRequest.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                String fieldName = fields[i].getName();
                Object fieldValue = fields[i].get(bean);
                map.put(fieldName, fieldValue);
            }
            return map;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 将Map数据转换为Bean
     *
     * @param type bean
     * @param map  数据集合
     * @param <T>  类类型
     * @return
     */
    public static <T> T copyToBean(Map<String, Object> map, Class<T> type) {
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
        }
        return null;
    }

    /**
     * 将Map数据转换为Bean
     *
     * @param type bean
     * @param map  数据集合
     * @param <T>  类类型
     * @return
     */
    public static <T> T copyToBeanAntiPattern(Map<String, Object> map, Class<T> type) {
        try {
            Field[] fields = type.getDeclaredFields();
            T beanClass = (T) type.getDeclaredConstructor().newInstance();
            for (int i = 0; i < fields.length; i++) {
                Object fieldName = fields[i].getName();
                if (map.containsKey(fieldName)) {
                    fields[i].set(beanClass, map.get(fieldName));
                }
            }
            return beanClass;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 深度拷贝
     *
     * @param obj 原始对象
     * @param <T> 对象类型
     * @return
     */
    public static <T> T deepCopy(T obj) {
        try {
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(obj);

            //反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return (T) ois.readObject();
        } catch (NotSerializableException exception) {
        } catch (Exception exception) {
        }
        return null;
    }

    /**
     * 判断是否是无需解析的值对象
     *
     * @param value 值对象
     * @return 是-true 否-false
     */
    public static boolean isFinal(final Object value) {
        if (Objects.isNull(value)) {
            return true;
        } else if (value instanceof String) {
            return true;
        } else if (value instanceof Integer) {
            return true;
        } else if (value instanceof Short) {
            return true;
        } else if (value instanceof Long) {
            return true;
        } else if (value instanceof Double) {
            return true;
        } else if (value instanceof Float) {
            return true;
        } else if (value instanceof Byte) {
            return true;
        } else if (value instanceof Boolean) {
            return true;
        } else if (value instanceof Character) {
            return true;
        } else if (value instanceof Number) {
            return true;
        } else if (value.getClass().isEnum()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 指定的修饰符是否序列化
     *
     * @param field 字段反射类型
     * @return
     */
    public static boolean isModifierFinal(final Field field) {
        int modifiers = field.getModifiers();
        return checkModifierFinalStaticTransVol(modifiers) || checkModifierNativeSyncStrict(modifiers);
    }

    protected static boolean checkModifierNativeSyncStrict(int modifiers) {
        return Modifier.isNative(modifiers)
                || Modifier.isSynchronized(modifiers)
                || Modifier.isStrict(modifiers);
    }

    protected static boolean checkModifierFinalStaticTransVol(int modifiers) {
        return Modifier.isFinal(modifiers)
                || Modifier.isStatic(modifiers)
                || Modifier.isTransient(modifiers)
                || Modifier.isVolatile(modifiers);
    }
}
