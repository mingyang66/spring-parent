package com.yaomy.control.aop.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.yaomy.control.aop.datasource.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: Description
 * @Version: 1.0
 */
@Configuration
public class InitDataSource {
    @Bean
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource defaultDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
    @Bean
    @ConfigurationProperties("first.datasource.druid")
    public DataSource firstDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource(){
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put("master", defaultDataSource());
        targetDataSources.put("first", firstDataSource());
        return DynamicDataSource.build(defaultDataSource(), targetDataSources);
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        //sqlSessionFactoryBean.setTypeAliasesPackage(typeAlias);
       // sqlSessionFactoryBean.setConfigLocation( new ClassPathResource(sqlmapConfigPath));
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //String packageSearchPath = PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX+"mapper/*.xml";
        String packageSearchPath = "classpath:mapper/*.xml";
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(packageSearchPath));
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return  sqlSessionFactoryBean;
    }

    @Bean(name = "jdbcTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

     @Bean
     public PlatformTransactionManager transactionManager() {
         DataSourceTransactionManager manager = new DataSourceTransactionManager(dynamicDataSource());
         return manager;
     }
}
