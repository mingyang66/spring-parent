package com.emily.infrastructure.datasource.service;

import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.mapper.MysqlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @TargetDataSource("mysql")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertMysql() {
        for (int i=0; i<2;i++){
            mysqlMapper.insertLocks("name"+i, "lock"+i);
            String lockName = mysqlMapper.findLocks("lock"+i);
            System.out.println("==>》查询到的lock名称是："+lockName);
            mysqlMapper.delLocks(lockName);
            System.out.println("==>》删除数据成功==>"+lockName);
        }
    }
}
