package com.emily.infrastructure.example;

import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.helper.SqlSessionFactoryHelper;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        long start = System.currentTimeMillis();
        SqlSessionFactory sqlSessionFactory = SqlSessionFactoryHelper.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
    /*        ItemMapper itemMapper = sqlSession.getMapper(ItemMapper.class);
            for (int i = 0; i < num; i++) {
                Item item = new Item();
                item.setLockName("a" + i);
                item.setScheName("B" + i);
                itemMapper.insertItem(item.getScheName(), item.getLockName());
                if (i % 1000 == 0) {
                    // 手动提交，提交后无法回滚
                    sqlSession.commit();
                }
            }*/
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


}
