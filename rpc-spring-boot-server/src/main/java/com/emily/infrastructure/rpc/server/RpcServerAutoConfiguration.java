package com.emily.infrastructure.rpc.server;

import com.emily.infrastructure.rpc.server.example.HelloServiceImpl;
import com.emily.infrastructure.rpc.server.annotation.RpcService;
import com.emily.infrastructure.rpc.server.handler.RpcServerHandler;
import com.emily.infrastructure.rpc.server.registry.RpcProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: RPC服务调用配置类
 * @author: Emily
 * @create: 2021/09/18
 */
@Configuration
public class RpcServerAutoConfiguration implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerAutoConfiguration.class);

    @PostConstruct
    public void startServer(){
        RpcServer.start(9999);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Object> beanMap = context.getBeansWithAnnotation(RpcService.class);
        beanMap.forEach((beanName, bean)->{
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            String interfaceName = interfaces[0].getSimpleName();
            logger.info("find rpc service {}", interfaceName);
            //将@RpcService标注的bean注入到注册表当中
            RpcProviderRegistry.registerServiceBean(interfaceName, bean);
        });
    }
}
