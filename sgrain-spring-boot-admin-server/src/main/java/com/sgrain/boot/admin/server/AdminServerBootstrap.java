package com.sgrain.boot.admin.server;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class AdminServerBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(AdminServerBootstrap.class, args);
    }

}
