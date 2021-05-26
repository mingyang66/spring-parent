package com.emily.infrastructure.test.service;

public interface NodeService {
    default void findNode(){
        return;
    }
    default void insertMysql(){
        return;
    }
}
