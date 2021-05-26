package com.emily.infrastructure.datasource.service;

public interface MysqlService {
    default void insertMysql(){
        return;
    }
}
