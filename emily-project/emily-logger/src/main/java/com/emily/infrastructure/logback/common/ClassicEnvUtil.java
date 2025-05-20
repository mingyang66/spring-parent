package com.emily.infrastructure.logback.common;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author :  Emily
 * @since :  2024/6/18 下午1:52
 */
public class ClassicEnvUtil {
    public static <T> List<T> loadFromServiceLoader(Class<T> c, ClassLoader classLoader) {
        ServiceLoader<T> loader = ServiceLoader.load(c, classLoader);
        List<T> listOfT = new ArrayList<>();
        for (T t : loader) {
            listOfT.add(t);
        }
        return listOfT;
    }
}
