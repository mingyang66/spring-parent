package com.yaomy.control.common.order;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.order.ApplicationContextUtil
 * @Date: 2019/8/2 11:22
 * @Version: 1.0
 */
public class ApplicationContextUtil implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

        String[] allBeanNames = applicationContext.getBeanDefinitionNames();
        for(String beanName : allBeanNames) {
            System.out.println(beanName);
        }
    }
}
