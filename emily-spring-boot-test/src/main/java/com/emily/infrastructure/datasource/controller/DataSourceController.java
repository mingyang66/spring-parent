package com.emily.infrastructure.datasource.controller;

import com.emily.infrastructure.autoconfigure.logger.common.LoggerUtils;
import com.emily.infrastructure.datasource.mapper.*;
import com.emily.infrastructure.datasource.po.Job;
import com.emily.infrastructure.datasource.po.Node;
import com.emily.infrastructure.datasource.po.SqlServer;
import com.emily.infrastructure.datasource.service.MysqlService;
import com.emily.infrastructure.datasource.service.NodeService;
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
    @Autowired
    private MysqlService mysqlService;
    @Autowired
    private NodeService nodeService;

    @GetMapping("getJob")
    public Job getJob(){
        LoggerUtils.module(DataSourceController.class, "/a/b/c", "info", "asdfffffffff");
        LoggerUtils.module(DataSourceController.class, "/a/b/c", "info1", "asdfffffffff");
        Job job = jobMapper.findJob();
        return job;
    }

    @GetMapping("getNode")
    public void getNode(){
        nodeService.findNode();
    }
    @GetMapping("getNode1")
    public Node getNode1(){
        return node1Mapper.findNode();
    }

    @GetMapping("getQuartzJob")
    public String getQuartzJob(){
        return mysqlMapper.findLocks("adsf");
    }

    @GetMapping("insertMysql")
    public String insertMysql(){
        mysqlService.insertMysql();
        return "success";
    }

    @GetMapping("findSqlServer")
    public SqlServer findSqlServer(){
        return sqlServerlMapper.findSqlServer();
    }
}
