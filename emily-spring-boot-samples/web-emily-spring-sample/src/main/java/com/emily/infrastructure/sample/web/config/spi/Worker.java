package com.emily.infrastructure.sample.web.config.spi;

/**
 * @author Emily
 * @program: spring-parent
 * 工人
 * @since 2021/11/30
 */
public class Worker implements People {

    public String getName() {
        return "工人";
    }
}
