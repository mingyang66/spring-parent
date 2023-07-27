package com.emily.infrastructure.test.spi;

import java.util.ServiceLoader;

/**
 * @program: spring-parent
 * 
 * @author Emily
 * @since 2021/11/30
 */
public class SpiTest {
    public static void main(String[] args) {
        ServiceLoader<People> serviceLoader = ServiceLoader.load(People.class);
        for (People people : serviceLoader) {
            //System.out.println(people.getName());
        }
    }
}
