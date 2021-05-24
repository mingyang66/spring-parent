package com.emily.framework.datasource.service;

import com.emily.framework.datasource.annotation.TargetDataSource;
import com.emily.framework.datasource.mapper.MysqlMapper;
import com.emily.framework.datasource.mapper.NodeMapper;
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
public class NodeServiceImpl implements NodeService{
    @Autowired
    private NodeMapper nodeMapper;
    @Autowired
    private MysqlMapper mysqlMapper;
    @Autowired
    private MysqlService mysqlService;
    @Autowired
    private NodeServiceImpl nodeService;
    @Override
    //@Transactional(rollbackFor = Exception.class)
    public void findNode() {
        nodeService.insertMysql();
        //mysqlMapper.findLocks("TEST2");
        nodeMapper.findNode();
       // insertMysql();

    }

    @TargetDataSource("mysql")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void insertMysql() {
        for (int i=0; i<2;i++){
            mysqlMapper.insertLocks("name"+i, "lock"+i);
            String lockName = mysqlMapper.findLocks("lock"+i);
            System.out.println("==>》查询到的lock名称是："+lockName);
            System.out.println();
            mysqlMapper.delLocks(lockName);
            System.out.println("==>》删除数据成功==>"+lockName);
        }
        mysqlMapper.insertLocks(System.currentTimeMillis()+"", Math.random()+"");
    }
}
