package com.emily.infrastructure.test;

import java.net.URL;


/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class FileHelper {

    public static ClassLoader getClassLoaderOfClass(final Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();
        if (cl == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            return cl;
        }
    }

    public static String getUrl(final Class<?> clazz, String name) {
        ClassLoader classLoader = getClassLoaderOfClass(clazz);
        URL url = classLoader.getResource(name);
        return url.toString();
    }
}
