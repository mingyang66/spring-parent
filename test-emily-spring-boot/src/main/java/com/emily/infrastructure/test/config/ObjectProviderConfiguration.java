package com.emily.infrastructure.test.config;

import com.emily.infrastructure.test.config.po.People;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Configuration;

/**
 * @program: spring-parent
 * @description: 依赖配置
 * @author: Emily
 * @create: 2021/11/20
 */
@Configuration
public class ObjectProviderConfiguration {

    public ObjectProviderConfiguration(ObjectProvider<People> user) {
        People user1 = user.orderedStream().findFirst().orElse(null);
        //System.out.println(user1.getUsername());
        People user2 = user.getIfAvailable();
    }

}
