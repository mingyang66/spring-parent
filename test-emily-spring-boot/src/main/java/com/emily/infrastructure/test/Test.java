/*
package com.emily.infrastructure.test;

import java.net.URL;
import java.util.Objects;

import static jdk.internal.loader.BootLoader.findResource;


*/
/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 *//*

public class Test {
    private ClassLoader parent;

    public URL getResource(String name) {
        Objects.requireNonNull(name);
        URL url;
        if (parent != null) {
            url = parent.getResource(name);
        } else {
            url = findResource(name);
        }
        if (url == null) {
            url = findResource(name);
        }
        return url;
    }

    public ClassLoader getClassLoaderOfClass(final Class<?> clazz) {
        ClassLoader cl = clazz.getClassLoader();
        if (cl == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            return cl;
        }
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.parent = test.getClassLoaderOfClass(Test.class);
        URL url = test.getResource("logback-test.xml");
        System.out.println(url.toString());
    }
}
*/
