package com.emily.infrastructure.rpc.server.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Emily
 */
@SpringBootApplication(scanBasePackages = "com.emily.infrastructure.rpc.server")
public class RpcServerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(RpcServerBootStrap.class, args);
    }

}
