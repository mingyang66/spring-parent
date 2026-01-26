package com.emily.sample.shell.command;

/**
 * @author :  Emily
 * @since :  2025/9/18 下午4:11
 */
//@ShellComponent
public class MyCommands {
    //@ShellMethod(key = "hello-world")
    /*public String helloWorld(@ShellOption(defaultValue = "spring") String arg) {
        return "Hello World " + arg;
    }*/

    //@ShellMethod(value = "Add numbers.", key = "sum")
    public int add(int a, int b) {
        return a + b;
    }
}
