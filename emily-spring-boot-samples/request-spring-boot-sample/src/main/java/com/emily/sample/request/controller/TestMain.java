package com.emily.sample.request.controller;

import java.util.function.Function;

/**
 * @author :  Emily
 * @since :  2024/12/27 下午5:56
 */
public class TestMain {
    public static void main(String[] args) {
        TestFunction function = () -> {

        };
    }
    public void test(Function<String, String> function) {
        function.andThen(new Function<String, Object>() {
            @Override
            public Object apply(String s) {
                    return "sd";
            }
        }).compose(new Function<String, String>() {
            @Override
            public String apply(String s) {
                return "";
            }
        }).apply("ds");
        System.out.println(function.apply("hello"));
    }
}
