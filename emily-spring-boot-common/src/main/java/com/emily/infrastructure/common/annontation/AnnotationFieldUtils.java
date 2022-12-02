package com.emily.infrastructure.common.annontation;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * @Description :  Field字段上注解解析类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/12/2 5:03 下午
 * todo 不成熟类，待验证
 */
public class AnnotationFieldUtils {
    /**
     * 判定指定字段上是否标记了指定注解
     *
     * @param field          字段对象
     * @param annotationType 注解类型
     * @param <T>
     * @return
     */
    public static <T extends Annotation> boolean hasAnnotation(Field field, Class<T> annotationType) {
        T annotation = getAnnotation(field, annotationType);
        if (annotation == null) {
            return false;
        }
        return true;
    }

    /**
     * 获取字段上指定的注解对象包括注解被指定注解标记
     *
     * @param field          字段对象
     * @param annotationType 注解类型
     * @param <T>
     * @return
     */
    public static <T extends Annotation> T getAnnotation(Field field, Class<T> annotationType) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        Annotation annotation = null;
        for (int i = 0; i < annotations.length; i++) {
            annotation = annotations[i];
            if (annotation.annotationType().equals(annotationType)) {
                break;
            }
            annotation = doGetAnnotation(annotation, annotationType);
        }
        if (annotation == null) {
            return null;
        }
        return (T) annotation;
    }

    /**
     * 获取指定注解对象是否是给定的注解类型
     *
     * @param ann            注解对象
     * @param annotationType 类型
     * @param <T>
     * @return
     */
    private static <T extends Annotation> T doGetAnnotation(Annotation ann, Class<T> annotationType) {
        Annotation[] annotations = ann.annotationType().getDeclaredAnnotations();
        Annotation annotation = null;
        for (int i = 0; i < annotations.length; i++) {
            annotation = annotations[i];
            if (annotation.annotationType().equals(Target.class)
                    || annotation.annotationType().equals(Retention.class)
                    || annotation.annotationType().equals(Inherited.class)
                    || annotation.annotationType().equals(Documented.class)) {
                continue;
            }
            if (annotation.annotationType().equals(annotationType)) {
                break;
            }
            return doGetAnnotation(annotation, annotationType);
        }
        if (annotation == null) {
            return null;
        }
        return (T) annotation;
    }
}
