package com.yaomy.common.order;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@Component
public class YellowPersion implements CommandLineRunner, Ordered {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----YellowPersion----");
    }
}
