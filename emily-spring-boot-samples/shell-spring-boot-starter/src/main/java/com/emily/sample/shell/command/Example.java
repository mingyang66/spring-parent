package com.emily.sample.shell.command;

import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.EnableCommand;

/**
 * @author :  姚明洋
 * @since :  2025/9/18 下午4:35
 */
@EnableCommand
public class Example {
    @Command(command = "example")
    public String example() {
        return "Hello World!";
    }
}
