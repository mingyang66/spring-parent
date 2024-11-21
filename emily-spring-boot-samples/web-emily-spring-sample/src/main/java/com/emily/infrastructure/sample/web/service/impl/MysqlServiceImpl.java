package com.emily.infrastructure.sample.web.service.impl;

import com.emily.infrastructure.sample.web.entity.World;
import com.emily.infrastructure.sample.web.mapper.mysql.MysqlMapper;
import com.emily.infrastructure.sample.web.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Emily
 * @program: spring-parent
 * @since 2021/05/13
 */
@Service
public class MysqlServiceImpl implements MysqlService {
    @Autowired
    private MysqlMapper mysqlMapper;

    @Override
    public List<World> getMysql() {
        return mysqlMapper.getMysql("田晓霞", "123456");
    }

    @Override
    public void insertMysql() {
        mysqlMapper.insertMysql("18321160687", "23");
    }
}
