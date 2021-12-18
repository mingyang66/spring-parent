package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.logback.LogbackProperties;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: spring-parent
 * @description: SPI控制器
 * @author: Emily
 * @create: 2021/12/17
 */
@RestController
@RequestMapping("api/spi")
public class SpiController {
    public String getSpi(){
        List<LogbackProperties> list = SpringFactoriesLoader.loadFactories(LogbackProperties.class, null);
        return null;
    }
}
