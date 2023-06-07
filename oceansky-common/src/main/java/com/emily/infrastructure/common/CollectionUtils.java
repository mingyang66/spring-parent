package com.emily.infrastructure.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @Description :  集合工具操作类类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/3 9:31 AM
 */
public abstract class CollectionUtils {
    /**
     * 判断集合是否为空
     *
     * @param collection 集合对象
     * @return true-是 false-否
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * 判断集合不为空
     *
     * @param collection 集合对象
     * @return true-是 false-否
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 判断Map集合是否为空
     *
     * @param map key-value集合
     * @return true-是 false-否
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    /**
     * 判断Map集合不为空
     *
     * @param map key-value集合
     * @return true-是 false-否
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 新建ArrayList数组
     *
     * @param <E> 数组中元素对象
     * @return 数组对象
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * 新建ArrayList数组
     *
     * @param elements 要加入新建数组中的元素对象
     * @param <E>      数组对象中的元素类型
     * @return 数组对象
     */
    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    /**
     * 判定集合中是否包含指定的元素
     *
     * @param collection 集合对象，可以为空
     * @param element    元素值
     * @param <E>        集合中元素类型
     * @return true-包含 false-不包含
     */
    public static <E> boolean contains(Collection<E> collection, E element) {
        if (isEmpty(collection)) {
            return false;
        }
        return collection.contains(element);
    }
}
