package com.emily.infrastructure.rpc.server;

import com.emily.infrastructure.rpc.server.connection.IRpcServerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * @program: spring-parent
 * @description: RPC服务调用配置类
 * @author: Emily
 * @create: 2021/09/18
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@Configuration
@EnableConfigurationProperties(IRpcServerProperties.class)
@ConditionalOnProperty(prefix = IRpcServerProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class IRpcServerAutoConfiguration implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(IRpcServerAutoConfiguration.class);

    @Bean(initMethod = "startServer")
    public IRpcServerConnection rpcServer(IRpcServerProperties properties) {
        return new IRpcServerConnection(properties.getPort());
    }


    @Override
    public void destroy() throws Exception {
        logger.info("<== 【销毁--自动化配置】----Rpc服务端销毁成功【RpcServerAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("==> 【初始化--自动化配置】----Rpc服务端启动成功【RpcServerAutoConfiguration】");
    }
}
