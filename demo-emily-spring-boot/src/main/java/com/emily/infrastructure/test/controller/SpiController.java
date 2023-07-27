package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.logger.LoggerFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: spring-parent
 *  SPI控制器
 * @author Emily
 * @since 2021/12/17
 */
@RestController
@RequestMapping("api/spi")
public class SpiController {
    public String getSpi() {
        List<LoggerFactory> list = SpringFactoriesLoader.loadFactories(LoggerFactory.class, null);
        return null;
    }
}
