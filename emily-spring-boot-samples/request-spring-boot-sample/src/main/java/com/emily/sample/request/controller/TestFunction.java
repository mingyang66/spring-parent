package com.emily.sample.request.controller;

/**
 * @author :  姚明洋
 * @since :  2024/12/27 下午5:38
 */
@FunctionalInterface
public interface TestFunction {
    void doTest();

    default boolean support(Object value) {
        return false;
    }
}
