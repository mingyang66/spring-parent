package com.emily.infrastructure.datasource.service;

public interface NodeService {
    default void findNode(){
        return;
    }
    default void insertMysql(){
        return;
    }
}
