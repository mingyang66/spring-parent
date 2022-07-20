package com.emily.infrastructure.test.service;

/**
 * @author Emily
 */
public interface MysqlService {
    /**
     *
     * @return
     */
    String getMysql();

    /**
     * 插入数据
     */
    void insertMysql() throws Exception;
}
