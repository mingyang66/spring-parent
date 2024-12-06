package com.emily.sample.desensitize.entity;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午4:12
 */
public class Factory {
    private String name;
    private Factory factory;
    private List<Worker> factories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Factory getFactory() {
        return factory;
    }

    public void setFactory(Factory factory) {
        this.factory = factory;
    }

    public List<Worker> getFactories() {
        return factories;
    }

    public void setFactories(List<Worker> factories) {
        this.factories = factories;
    }
}
