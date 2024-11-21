package com.emily.infrastructure.sample.web.service;

import com.emily.infrastructure.sample.web.entity.World;

import java.util.List;

/**
 * @author Emily
 */
public interface MysqlService {

    List<World> getMysql();

    void insertMysql();
}
