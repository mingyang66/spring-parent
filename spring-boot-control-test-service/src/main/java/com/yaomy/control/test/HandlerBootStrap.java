package com.yaomy.control.test;


import com.yaomy.control.test.zeromq.SocketTest;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.yaomy.control"})
@MapperScan(basePackages = {"com.yaomy.control.*.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class HandlerBootStrap {

    @Resource(name = "defaultThreadPool")
    private ThreadPoolTaskExecutor threadPool;
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        try {
           // String endPoint = "tcp://127.0.0.1:5000";
            //String endPoint = "tcp://10.10.81.224:5000";
            //String endPoint = "tcp://172.30.67.121:8200";
            //new Thread(()->new ZeroMQServer(endPoint).start()).start();
           // new Thread(()->new ZeroMQClient(endPoint).build()).start();

        } catch (Exception e){

        }
        /**
         * 启动JAVA SOCKET服务器和客户端
         */
        SocketTest.start(threadPool, "127.0.0.1", 5004);
        return new RestTemplate();
    }
}
