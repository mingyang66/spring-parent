package com.emily.framework.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.emily.framework.common.enums.AppHttpStatus;
import com.emily.framework.common.exception.BusinessException;
import com.emily.framework.common.logger.LoggerUtils;
import com.emily.framework.datasource.context.DynamicMultipleDataSources;
import com.emily.framework.datasource.interceptor.DataSourceMethodInterceptor;
import com.emily.framework.common.enums.AopOrderEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 控制器切点配置
 * @Author Emily
 * @Version: 1.0
 */
@Configuration
@AutoConfigureBefore(DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnProperty(prefix = "spring.emily.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceAutoConfiguration implements InitializingBean, DisposableBean {

    public static final String DATA_SOURCE_BEAN_NAME = "dataSourcePointCutAdvice";
    /**
     * Mybatis xml映射文件配置
     */
    public static final String MYBATIS_LOCATION_MAPPING = "mybatis.mapper-locations";
    /**
     * Mybatis config location
     */
    public static final String MYBATIS_CONFIG_LOCATION = "mybatis.config-location";
    /**
     * mybatis.type-aliases-package
     */
    public static final String MYBATIS_TYPE_ALIASES_PACKAGE = "mybatis.type-aliases-package";
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = StringUtils.join("@annotation(com.emily.framework.datasource.annotation.TargetDataSource) ");
    /**
     * 配置文件对象
     */
    @Autowired
    private Environment environment;

    /**
     * 方法切入点函数：execution(<修饰符模式>? <返回类型模式> <方法名模式>(<参数模式>) <异常模式>?)  除了返回类型模式、方法名模式和参数模式外，其它项都是可选的
     * 切入点表达式：
     * 第一个*号：表示返回类型，*号表示所有的类型
     * 包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包下的所有子包
     * 第二个*号：表示类名，*号表示所有的类名
     * 第三个*号：表示方法名，*号表示所有的方法，后面的括弧表示方法里面的参数，两个句点表示任意参数
     */
    @Bean(DATA_SOURCE_BEAN_NAME)
    @ConditionalOnClass(value = {DataSourceMethodInterceptor.class})
    public DefaultPointcutAdvisor defaultPointcutAdvisor(DataSourceProperties dataSourceProperties) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //获取切面表达式
        pointcut.setExpression(DEFAULT_POINT_CUT);
        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(new DataSourceMethodInterceptor(dataSourceProperties));
        advisor.setOrder(AopOrderEnum.DATASOURCE_AOP.getOrder());
        return advisor;
    }

    /**
     * 设置多数据源
     *
     * @return
     */
    @Bean("dynamicMultipleDataSources")
    public DataSource dynamicMultipleDataSources(DataSourceProperties dataSourceProperties) {
        Map<String, DruidDataSource> configs = dataSourceProperties.getConfig();
        if (configs.isEmpty()) {
            throw new BusinessException(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), "数据库配置不存在");
        }
        if (!configs.containsKey(dataSourceProperties.getDefaultConfig())) {
            throw new BusinessException(AppHttpStatus.DATABASE_EXCEPTION.getStatus(), "默认数据库必须配置");
        }
        Map<Object, Object> targetDataSources = new HashMap<>(configs.size());
        configs.keySet().forEach(key -> {
            targetDataSources.put(key, configs.get(key));
        });
        return DynamicMultipleDataSources.build(dataSourceProperties.getDefaultDataSource(), targetDataSources);
    }

    /**
     * 创建SqlSessionFactoryBean
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dynamicMultipleDataSources") DataSource dynamicMultipleDataSources) throws Exception {

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
        if (environment.containsProperty(MYBATIS_TYPE_ALIASES_PACKAGE) && StringUtils.isNotBlank(environment.getProperty(MYBATIS_TYPE_ALIASES_PACKAGE))) {
            sqlSessionFactoryBean.setTypeAliasesPackage(environment.getProperty(MYBATIS_TYPE_ALIASES_PACKAGE));
        }
        /**
         * 设置mybatis配置
         */
        if (environment.containsProperty(MYBATIS_CONFIG_LOCATION) && StringUtils.isNotBlank(environment.getProperty(MYBATIS_CONFIG_LOCATION))) {
            sqlSessionFactoryBean.setConfigLocation(new ClassPathResource(environment.getProperty(MYBATIS_CONFIG_LOCATION)));
        }
        /**
         * 设置sql xml映射文件配置
         */
        if (environment.containsProperty(MYBATIS_LOCATION_MAPPING) && StringUtils.isNotBlank(environment.getProperty(MYBATIS_LOCATION_MAPPING))) {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sqlSessionFactoryBean.setMapperLocations(resolver.getResources(environment.getProperty(MYBATIS_LOCATION_MAPPING)));
        }
        /**
         * 设置数据源
         */
        sqlSessionFactoryBean.setDataSource(dynamicMultipleDataSources);

        return sqlSessionFactoryBean;
    }

    /**
     * 生成SqlSessionTemplate对象
     *
     * @param sqlSessionFactory
     * @return
     */
    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * PlatformTransactionManager:这是Spring事务基础设施中的核心接口，应用程序可以直接使用它，应用程序可以使用TransactionTemplate或者AOP进行声明式事务划分；
     */
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynamicMultipleDataSources") DataSource dynamicMultipleDataSources) {
        /**
         * DataSourceTransactionManager:这个类可以在任何环境中使用任何JDBC驱动，
         */
        return new DataSourceTransactionManager(dynamicMultipleDataSources);
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(DataSourceAutoConfiguration.class, "【销毁--自动化配置】----数据库多数据源组件【DataSourceAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(DataSourceAutoConfiguration.class, "【初始化--自动化配置】----数据库多数据源组件【DataSourceAutoConfiguration】");
    }
}
