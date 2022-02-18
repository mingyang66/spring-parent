package com.emily.infrastructure.datasource.helper;

import com.emily.infrastructure.core.context.ioc.IOCContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

/**
 * @program: spring-parent
 * @description: SqlSessionFactory工厂帮助类
 * @author: Emily
 * @create: 2021/09/14
 */
public class SqlSessionFactoryHelper {
    /**
     * 获取SqlSessionFactory 工厂bean对象
     *
     * @return SqlSessionFactory
     */
    public static SqlSessionFactory getSqlSessionFactory() {
        SqlSessionTemplate sqlSessionTemplate = IOCContext.getBean(SqlSessionTemplate.class);
        return sqlSessionTemplate.getSqlSessionFactory();
    }
}
