package com.emily.infrastructure.test.service;

import com.emily.infrastructure.test.entity.World;

import java.util.List;

/**
 * @author Emily
 */
public interface MysqlService {

    List<World> getMysql();

    void insertMysql();
}
