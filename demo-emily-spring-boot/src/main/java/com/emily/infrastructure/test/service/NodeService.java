package com.emily.infrastructure.test.service;

public interface NodeService {
    default void findNode() throws Exception {
        return;
    }

    default void instertStatus() {
        return;
    }

    default void insertMysql() throws Exception {
        return;
    }

    String getOracle();
    String getMysql();
    String getTwoDb();
}
