package com.emily.infrastructure.test.service;

import com.emily.infrastructure.test.mapper.MysqlMapper;
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
    public void insertMysql() {
        int i = 0;
       // mysqlMapper.insertLocks("name" + i, "lock" + i);
        String lockName = mysqlMapper.findLocks("lock" + i);
        System.out.println("==>》查询到的lock名称是：" + lockName);
        //mysqlMapper.delLocks(lockName);
        //System.out.println("==>》删除数据成功==>" + lockName);
    }

    @Override
    public String getMysql() {
        return mysqlMapper.getMysql();
    }
}
