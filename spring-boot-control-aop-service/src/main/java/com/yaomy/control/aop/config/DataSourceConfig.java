package com.yaomy.control.aop.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.yaomy.control.aop.constant.DbType;
import com.yaomy.control.aop.datasource.DynamicDataSource;
import com.yaomy.control.common.control.constant.MybatisConstant;
import com.yaomy.control.conf.properties.PropertyService;
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
 * @Description: 多数据源初始化配置
 * sqlSessionTemplateRef:指定使用哪个SqlSessionTemplate，通常是在Spring Context有多个数据源的时候才使用
 * @MapperScan：在使用java配置文件时，使用此注解来注册Mybatis映射器接口，它是MapperScannerConfigurer和MapperScannerRegistrar的工作原理是一样的
 * @Version: 1.0
 */
@Configuration
public class DataSourceConfig {
    /**
     * 配置文件对象
     */
    @Autowired
    private PropertyService propertyService;

    /**
     * 默认数据源,if not found prefix 'spring.datasource.druid' jdbc properties ,'spring.datasource' prefix jdbc properties will be used.
     */
    @Bean("defaultDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource defaultDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    /**
     *  设置多数据源
     * @param defaultDataSource 默认数据源
     * @return
     */
    @Bean("dynamicDataSource")
    public DataSource dynamicDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource){
        Map<Object, Object> targetDataSources = new HashMap<>(1);
        targetDataSources.put(DbType.DEFAULT_DATASOURCE, defaultDataSource);
        return DynamicDataSource.build(defaultDataSource, targetDataSources);
    }

    /**
     * 创建SqlSessionFactoryBean
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) throws Exception{

        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        /**
         * 对于spring-boot项目使用java代码方法配置数据源时，mybatis核心库在springboot打成jar包后有个bug,无法完成别名的扫描，在低版本的mybatis-spring-boot-starter
         * 中需要自己集成MYbatis核心库中的VFS重写它原有资源的加载方式，在高版本的mybatis-spring-boot-starter中已经帮我们实现了一个叫SpringBootVFS的类；
         * 感兴趣的可以去官网查看合格BUG:https://github.com/mybatis/spring-boot-starter/issues/177
         */
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        /**
         * 设置mybatis别名
         */
        if(propertyService.containsProperty(MybatisConstant.MYBATIS_TYPPE_ALIASES_PACKAGE) && StringUtils.isNotBlank(propertyService.getProperty(MybatisConstant.MYBATIS_TYPPE_ALIASES_PACKAGE))){
            sqlSessionFactoryBean.setTypeAliasesPackage(propertyService.getProperty(MybatisConstant.MYBATIS_TYPPE_ALIASES_PACKAGE));
        }
        /**
         * 设置mybatis配置
         */
        if(propertyService.containsProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION) && StringUtils.isNotBlank(propertyService.getProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION))){
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(propertyService.getProperty(MybatisConstant.MYBATIS_CONFIG_LOCATION)));
        }
        /**
         * 设置sql xml映射文件配置
         */
        if(propertyService.containsProperty(MybatisConstant.MYBATIS_LOCATION_MAPPING) && StringUtils.isNotBlank(propertyService.getProperty(MybatisConstant.MYBATIS_LOCATION_MAPPING))){
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(propertyService.getProperty(MybatisConstant.MYBATIS_LOCATION_MAPPING)));
        }
        /**
         * 设置数据源
         */
        sqlSessionFactoryBean.setDataSource(dynamicDataSource);

        return  sqlSessionFactoryBean;
    }

    /**
     * 生成SqlSessionTemplate对象
     * @param sqlSessionFactory
     * @return
     */
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory){
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * PlatformTransactionManager:这是Spring事务基础设施中的核心接口，应用程序可以直接使用它，应用程序可以使用TransactionTemplate或者AOP进行声明式事务划分；
     */
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dynamicDataSource) {
        /**
         * DataSourceTransactionManager:这个类可以在任何环境中使用任何JDBC驱动，
         */
        return new DataSourceTransactionManager(dynamicDataSource);
    }
}
