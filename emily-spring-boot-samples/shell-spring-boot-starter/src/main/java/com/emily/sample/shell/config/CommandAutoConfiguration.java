package com.emily.sample.shell.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.shell.command.CommandRegistration;

/**
 * @author :  姚明洋
 * @since :  2025/9/18 下午4:33
 */
@AutoConfiguration
public class CommandAutoConfiguration {
    @Bean
    CommandRegistration commandRegistration() {
        return CommandRegistration.builder()
                .command("mycommand")
                .build();
    }
}
