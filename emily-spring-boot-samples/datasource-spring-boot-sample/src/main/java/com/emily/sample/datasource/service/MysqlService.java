package com.emily.sample.datasource.service;

import com.emily.sample.datasource.entity.World;

import java.util.List;

/**
 * @author Emily
 */
public interface MysqlService {

    List<World> getMysql();

    void insertMysql();
}
