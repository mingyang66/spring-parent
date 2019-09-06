package com.yaomy.control.aop.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.yaomy.control.aop.datasource.DynamicDataSource;
import com.yaomy.control.common.control.conf.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
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
    /**
     * 配置文件对象
     */
    @Autowired
    private PropertyService propertyService;
    /**
     * Mybatis xml映射文件配置
     */
    private static final String MYBATIS_LOCATION_MAPPING = "mybatis.mapper-locations";
    /**
     * Mybatis config location
     */
    private static final String MYBATIS_CONFIG_LOCATION = "mybatis.config-location";
    /**
     * mybatis.type-aliases-package
     */
    private static final String MYBATIS_TYPPE_ALIASES_PACKAGE = "mybatis.type-aliases-package";
    /**
     * 默认数据源
     */
    private static final String DEFAULT_DATASOURCE = "spring";

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
    @Primary
    public DataSource dynamicDataSource(){
        Map<Object, Object> targetDataSources = new HashMap<>(2);
        targetDataSources.put("spring", defaultDataSource());
        targetDataSources.put("first", firstDataSource());
        targetDataSources.put("second", secondDataSource());
        return DynamicDataSource.build(defaultDataSource(), targetDataSources);
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws Exception{
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        if(propertyService.containsProperty(MYBATIS_TYPPE_ALIASES_PACKAGE) && StringUtils.isNotBlank(propertyService.getProperty(MYBATIS_TYPPE_ALIASES_PACKAGE))){
            sqlSessionFactoryBean.setTypeAliasesPackage(propertyService.getProperty(MYBATIS_TYPPE_ALIASES_PACKAGE));
        }

        if(propertyService.containsProperty(MYBATIS_CONFIG_LOCATION) && StringUtils.isNotBlank(propertyService.getProperty(MYBATIS_CONFIG_LOCATION))){
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(propertyService.getProperty(MYBATIS_CONFIG_LOCATION)));
        }
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources(propertyService.getProperty(MYBATIS_LOCATION_MAPPING, DEFAULT_DATASOURCE)));
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
