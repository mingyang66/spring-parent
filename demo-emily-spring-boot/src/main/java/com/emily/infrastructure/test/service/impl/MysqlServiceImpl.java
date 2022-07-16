package com.emily.infrastructure.test.service.impl;

import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.datasource.annotation.TargetTransactional;
import com.emily.infrastructure.test.mapper.mysql.MysqlMapper;
import com.emily.infrastructure.test.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/13
 */
@Service
public class MysqlServiceImpl implements MysqlService {
    @Autowired
    private MysqlMapper mysqlMapper;

    @Override
    public String getMysql() {
        return mysqlMapper.getMysql("123123");
    }

    @Override
    @TargetTransactional(value = "mysql", readOnly = false, rollbackFor = BusinessException.class)
    public void insertMysql() {
        for (int i = 0; i < 1000; i++) {
            mysqlMapper.insertLocks("schedName" + i, "lockName" + i);
            if (i == 100) {
                int s = i / 0;
            }
        }

    }
}
