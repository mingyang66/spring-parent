package com.yaomy.control.common.order;

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
public class BlackPersion implements CommandLineRunner, Ordered {
    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void run(String... args) throws Exception {
        //System.out.println("----BlackPersion----");
    }
}
