package com.emily.infrastructure.desensitize.test.entity;

import com.emily.infrastructure.desensitize.annotation.DesensitizeModel;
import com.emily.infrastructure.desensitize.annotation.DesensitizeProperty;

import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2024/12/10 下午11:12
 */
public class Company {
    private Long id;
    private String name;
    private String address;
    private Worker worker;
    private List<Worker> list;
    private Map<String, Worker> dataMap;

    public List<Worker> getList() {
        return list;
    }

    public void setList(List<Worker> list) {
        this.list = list;
    }

    public Map<String, Worker> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Worker> dataMap) {
        this.dataMap = dataMap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    @DesensitizeModel
    public static class Worker {
        private Long id;
        @DesensitizeProperty
        private String name;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
