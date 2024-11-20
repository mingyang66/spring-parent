package com.emily.infrastructure.sample.web.config.spi;

import java.util.ServiceLoader;

/**
 * @author Emily
 * @program: spring-parent
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
