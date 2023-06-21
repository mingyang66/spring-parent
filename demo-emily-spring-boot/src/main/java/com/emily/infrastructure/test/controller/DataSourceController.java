package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.core.helper.RequestUtils;
import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.helper.SqlSessionFactoryHelper;
import com.emily.infrastructure.date.DateComputeUtils;
import com.emily.infrastructure.test.mapper.mysql.ItemMapper;
import com.emily.infrastructure.test.mapper.mysql.JobMapper;
import com.emily.infrastructure.test.po.Item;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.World;
import com.emily.infrastructure.test.service.MysqlService;
import com.emily.infrastructure.test.service.OracleService;
import com.google.common.collect.Lists;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
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
    private ItemMapper itemMapper;
    @Autowired
    private OracleService oracleService;
    @Autowired
    private MysqlService mysqlService;

    /**
     * foreach 模式批量插入数据库
     *
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
        Instant start = Instant.now();
        itemMapper.inertByBatch(list);
        return DateComputeUtils.minusMillis(Instant.now(), start);
    }

    /**
     * batch模式批量插入数据库
     *
     * @param num
     * @return
     */
    @GetMapping("batch/{num}")
    @TargetDataSource("mysql")
    @Transactional(rollbackFor = Exception.class)
    public long getBatch(@PathVariable Integer num) {
        Instant start = Instant.now();
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryHelper.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            ItemMapper itemMapper = sqlSession.getMapper(ItemMapper.class);
            for (int i = 0; i < num; i++) {
                Item item = new Item();
                item.setLockName("a" + i);
                item.setScheName("B" + i);
                itemMapper.insertItem(item.getScheName(), item.getLockName());
                if (i % 1000 == 0) {
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
        return DateComputeUtils.minusMillis(Instant.now(), start);
    }

    /**
     * 逐条插入数据库
     *
     * @param num
     * @return
     */
    @GetMapping("insertItem/{num}")
    @TargetDataSource("mysql")
    public long insertItem(@PathVariable Integer num) {
        Instant start = Instant.now();
        for (int i = 0; i < num; i++) {
            Item item = new Item();
            item.setLockName("a" + i);
            item.setScheName("B" + i);
            itemMapper.insertItem(item.getScheName(), item.getLockName());
        }
        return DateComputeUtils.minusMillis(Instant.now(), start);
    }

    @GetMapping("getJob")
    public Job getJob() {
        Job job = jobMapper.findJob();
        return job;
    }


    @GetMapping("getOracle")
    public String getOracle() {
        return oracleService.getOracle();
    }

    @GetMapping("getMysql")
    public List<World> getMysql() {
        return mysqlService.getMysql();
    }

    @GetMapping("insertMysql")
    public void insertMysqlBatch() throws Exception {
        mysqlService.insertMysql();
    }

    @GetMapping("getTarget")
    public String getTarget() {
        return oracleService.getTarget(RequestUtils.getRequest().getParameter("param"));
    }

    @GetMapping("getSql")
    public String getSql() {
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryHelper.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);

        String id = "com.emily.infrastructure.test.mapper.mysql.MysqlMapper.insertMysql";
        MappedStatement statement = sqlSession.getConfiguration().getMappedStatement(id);
        String sql = statement.getBoundSql("schedName").getSql();
        return sql;
    }
}
