package com.emily.framework.datasource.controller;

import com.emily.framework.datasource.mapper.*;
import com.emily.framework.datasource.po.Job;
import com.emily.framework.datasource.po.Node;
import com.emily.framework.datasource.po.QuartzJob;
import com.emily.framework.datasource.po.SqlServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description: 数据源测试
 * @author: Emily
 * @create: 2021/04/22
 */
@RestController
@RequestMapping("api")
public class DataSourceController {

    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private NodeMapper nodeMapper;
    @Autowired
    private Node1Mapper node1Mapper;
    @Autowired
    private MysqlMapper mysqlMapper;
    @Autowired
    private SqlServerlMapper sqlServerlMapper;
    @GetMapping("getJob")
    public Job getJob(){
        Job job = jobMapper.findJob();
        return job;
    }

    @GetMapping("getNode")
    public Node getNode(){
        return nodeMapper.findNode();
    }
    @GetMapping("getNode1")
    public Node getNode1(){
        return node1Mapper.findNode();
    }

    @GetMapping("getQuartzJob")
    public QuartzJob getQuartzJob(){
        return mysqlMapper.findQuartzJob();
    }

    @GetMapping("findSqlServer")
    public SqlServer findSqlServer(){
        return sqlServerlMapper.findSqlServer();
    }
}
