package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.helper.SqlSessionFactoryHelper;
import com.emily.infrastructure.test.mapper.*;
import com.emily.infrastructure.test.po.Item;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.Node;
import com.emily.infrastructure.test.po.SqlServer;
import com.emily.infrastructure.test.service.MysqlService;
import com.emily.infrastructure.test.service.NodeService;
import com.google.common.collect.Lists;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    private SlaveMapper nodeMapper;
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
    @Autowired
    private ItemMapper itemMapper;

    /**
     * foreach 模式批量插入数据库
     * @param num
     * @return
     */
    @GetMapping("batchSimple/{num}")
    @TargetDataSource("mysql")
    public long batchSimple(@PathVariable Integer num) {
        List<Item> list = Lists.newArrayList();
        for (int i = 0; i < num; i++) {
            Item item = new Item();
            item.setLockName("a" + i);
            item.setScheName("B" + i);
            list.add(item);
        }
        long start = System.currentTimeMillis();
        itemMapper.inertByBatch(list);
        return System.currentTimeMillis() - start;
    }

    /**
     * batch模式批量插入数据库
     * @param num
     * @return
     */
    @GetMapping("batch/{num}")
    @TargetDataSource("mysql")
    @Transactional(rollbackFor = Exception.class)
    public long getBatch(@PathVariable Integer num) {
        long start = System.currentTimeMillis();
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryHelper.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            ItemMapper itemMapper = sqlSession.getMapper(ItemMapper.class);
            for (int i = 0; i < num; i++) {
                Item item = new Item();
                item.setLockName("a" + i);
                item.setScheName("B" + i);
                itemMapper.insertItem(item.getScheName(), item.getLockName());
                if(i%1000==0){
                    // 手动提交，提交后无法回滚
                    sqlSession.commit();
                }
            }
            // 手动提交，提交后无法回滚
            sqlSession.commit();
            // 清理缓存，防止溢出
            sqlSession.clearCache();
        } catch (Exception exception) {
            // 没有提交的数据可以回滚
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return System.currentTimeMillis() - start;
    }

    /**
     * 逐条插入数据库
     * @param num
     * @return
     */
    @GetMapping("insertItem/{num}")
    @TargetDataSource("mysql")
    public long insertItem(@PathVariable Integer num) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            Item item = new Item();
            item.setLockName("a" + i);
            item.setScheName("B" + i);
            itemMapper.insertItem(item.getScheName(), item.getLockName());
        }
        return System.currentTimeMillis() - start;
    }

    @GetMapping("getJob")
    public Job getJob() {
        //LoggerFactory.module("/a/b/c", "info", "asdfffffffff");
        //LoggerFactory.module("/a/b/c", "info1", "asdfffffffff");
        Job job = jobMapper.findJob();
        return job;
    }

    @GetMapping("getNode")
    public void getNode() throws Exception {
        nodeService.findNode();
    }

    @GetMapping("getNode1")
    public Node getNode1() {
        return node1Mapper.findNode();
    }

    @GetMapping("getQuartzJob")
    public String getQuartzJob() {
        return mysqlMapper.findLocks("0.47536746927909124");
    }

    @GetMapping("insertMysql")
    public String insertMysql() {
        mysqlService.insertMysql();
        return "success";
    }

    @GetMapping("findSqlServer")
    public SqlServer findSqlServer() {
        return sqlServerlMapper.findSqlServer();
    }

    @Autowired
    private ChildMapper childMapper;

    @GetMapping("getParent")
    public Job getParent(){
       return childMapper.get();
    }

    @GetMapping("getChild")
    public Job getChild(){
        return childMapper.getJob();
    }
}
