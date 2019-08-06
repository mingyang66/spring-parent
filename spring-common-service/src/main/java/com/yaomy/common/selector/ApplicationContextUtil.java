package com.yaomy.common.selector;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.selector.ApplicationContextUtil
 * @Author: 姚明洋
 * @Date: 2019/8/6 10:59
 * @Version: 1.0
 */
@Component
@Import(DogImportSelector.class)
public class ApplicationContextUtil implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        String[] names = applicationContext.getBeanDefinitionNames() ;
        for (String name:names
             ) {
            //System.out.println(name);
        }
    }
}
