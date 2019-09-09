package com.yaomy.control.aop.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.yaomy.control.aop.datasource.DynamicDataSource;
import com.yaomy.control.common.control.MybatisConstant;
import com.yaomy.control.common.control.conf.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 多数据源初始化配置
 * @Version: 1.0
 */
@Configuration
@MapperScan(value = "com.yaomy.control.test.mapper", sqlSessionTemplateRef = "jdbcTemplate")
public class DataSourceConfig {
    /**
     * 配置文件对象
     */
    @Autowired
    private PropertyService propertyService;

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
    @ConfigurationProperties("second.datasource.druid")
    public DataSource secondDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    public DataSource dynamicDataSource(){
        Map<Object, Object> targetDataSources = new HashMap<>(3);
        targetDataSources.put("spring", defaultDataSource());
        targetDataSources.put("first", firstDataSource());
        targetDataSources.put("second", secondDataSource());
        return DynamicDataSource.build(defaultDataSource(), targetDataSources);
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        if(propertyService.containsProperty(MybatisConstant.MYBATIS_TYPPE_ALIASES_PACKAGE) && StringUtils.isNotBlank(propertyService.getProperty(MybatisConstant.MYBATIS_TYPPE_ALIASES_PACKAGE))){
            sqlSessionFactoryBean.setTypeAliasesPackage(propertyService.getProperty(MybatisConstant.MYBATIS_TYPPE_ALIASES_PACKAGE));
        }

        if(propertyService.containsProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION) && StringUtils.isNotBlank(propertyService.getProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION))){
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(propertyService.getProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION)));
        }
        if(propertyService.containsProperty(MybatisConstant.MYBATIS_LOCATION_MAPPING) && StringUtils.isNotBlank(propertyService.getProperty(MybatisConstant.MYBATIS_LOCATION_MAPPING))){
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(propertyService.getProperty(MybatisConstant.MYBATIS_LOCATION_MAPPING)));
        }
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
