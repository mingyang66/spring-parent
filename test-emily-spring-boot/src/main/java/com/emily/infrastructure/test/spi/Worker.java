package com.emily.infrastructure.test.spi;

/**
 * @program: spring-parent
 * @description: 工人
 * @author: Emily
 * @create: 2021/11/30
 */
public class Worker implements People {
    @Override
    public String getName() {
        return "工人";
    }
}
