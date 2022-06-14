package com.emily.infrastructure.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.spring.boot.autoconfigure.stat.DruidSpringAopConfiguration;
import com.emily.infrastructure.common.constant.AopOrderInfo;
import com.emily.infrastructure.core.aop.advisor.AnnotationPointcutAdvisor;
import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.context.DynamicRoutingDataSource;
import com.emily.infrastructure.datasource.interceptor.DataSourceCustomizer;
import com.emily.infrastructure.datasource.interceptor.DefaultDataSourceMethodInterceptor;
import com.emily.infrastructure.datasource.thread.DataSourceDaemonThread;
import com.emily.infrastructure.logger.LoggerFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;
import org.springframework.util.Assert;

import javax.sql.DataSource;

/**
 * Oracle数据库PSCache解决方案：https://github.com/alibaba/druid/wiki/Oracle%E6%95%B0%E6%8D%AE%E5%BA%93%E4%B8%8BPreparedStatementCache%E5%86%85%E5%AD%98%E9%97%AE%E9%A2%98%E8%A7%A3%E5%86%B3%E6%96%B9%E6%A1%88
 *
 * @Description: 控制器切点配置
 * @Author Emily
 * @since 4.0.8
 */
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@AutoConfiguration(before = DruidDataSourceAutoConfigure.class)
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnProperty(prefix = DataSourceProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceAutoConfiguration implements BeanFactoryPostProcessor, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceAutoConfiguration.class);


    /**
     * 数据源切面增强类，支持@TargetDataSource注解标注在父类、接口、父类或接口的方法上都可以拦截到
     *
     * @param dataSourceCustomizers 切面|拦截器
     * @param properties            属性配置
     * @return 切面增强类
     * @since(4.0.6)
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Advisor dataSourcePointCutAdvice(ObjectProvider<DataSourceCustomizer> dataSourceCustomizers, DataSourceProperties properties) {
        //限定类级别的切点
        Pointcut cpc = new AnnotationMatchingPointcut(TargetDataSource.class, properties.isCheckInherited());
        //限定方法级别的切点
        Pointcut mpc = new AnnotationMatchingPointcut(null, TargetDataSource.class, properties.isCheckInherited());
        //组合切面(并集)，即只要有一个切点的条件符合，则就拦截
        Pointcut pointcut = new ComposablePointcut(cpc).union(mpc);
        //切面增强类
        AnnotationPointcutAdvisor advisor = new AnnotationPointcutAdvisor(dataSourceCustomizers.orderedStream().findFirst().get(), pointcut);
        //切面优先级顺序
        advisor.setOrder(AopOrderInfo.DATASOURCE);
        //数据源守护线程
        createDataSourceDaemonThread(properties);
        return advisor;
    }

    @Bean
    @ConditionalOnMissingBean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataSourceCustomizer dataSourceCustomizer(DataSourceProperties properties) {
        return new DefaultDataSourceMethodInterceptor(properties);
    }

    /**
     * 从配置文件中获取多数据源配置信息
     * {@link DataSourceTransactionManagerAutoConfiguration}
     * {@link MybatisAutoConfiguration}
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public DataSource dynamicRoutingDataSource(DataSourceProperties properties) {
        Assert.notNull(properties.getDefaultTargetDataSource(), "默认数据库必须配置");
        //动态切换多数据源对象
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource();
        //如果存在默认数据源，指定默认的目标数据源；映射的值可以是javax.sql.DataSource或者是数据源（data source）字符串；如果setTargetDataSources指定的数据源不存在，将会使用默认的数据源
        dynamicRoutingDataSource.setDefaultTargetDataSource(properties.getDefaultTargetDataSource());
        //指定目标数据源的Map集合映射，使用查找键（Look Up Key）作为Key,这个Map集合的映射Value可以是javax.sql.DataSource或者是数据源（data source）字符串；集合的Key可以为任何数据类型，当前类会通过泛型的方式来实现查找，
        dynamicRoutingDataSource.setTargetDataSources(properties.getTargetDataSources());
        //是否对默认数据源执行宽松回退，即：当目标数据源找不到时回退到默认数据源，默认：true
        dynamicRoutingDataSource.setLenientFallback(properties.isLenientFallback());
        //设置DataSourceLookup为解析数据源的字符串，默认是使用JndiDataSourceLookup；允许直接指定应用程序服务器数据源的JNDI名称；
        dynamicRoutingDataSource.setDataSourceLookup(null);
        //将设置的默认数据源、目标数据源解析为真实的数据源对象赋值给resolvedDefaultDataSource变量和resolvedDataSources变量
        dynamicRoutingDataSource.afterPropertiesSet();
        return dynamicRoutingDataSource;
    }

    /**
     * 将指定的bean 角色标记为基础设施类型，相关提示类在 org.springframework.context.support.PostProcessorRegistrationDelegate
     *
     * @param beanFactory
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForType(DataSourceProperties.class);
        if (beanNames.length > 0 && beanFactory.containsBeanDefinition(beanNames[0])) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanNames[0]);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }

        if (beanFactory.containsBeanDefinition(DruidSpringAopConfiguration.class.getName())) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(DruidSpringAopConfiguration.class.getName());
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }

        beanNames = beanFactory.getBeanNamesForType(DruidStatProperties.class);
        if (beanNames.length > 0 && beanFactory.containsBeanDefinition(beanNames[0])) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanNames[0]);
            beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        }

    }

    /**
     * 创建守护线程
     *
     * @param properties
     */
    private void createDataSourceDaemonThread(DataSourceProperties properties) {
        //数据源守护线程
        Thread thread = new DataSourceDaemonThread("dataSource", properties);
        thread.start();
    }

    @Override
    public void destroy() {
        logger.info("<== 【销毁--自动化配置】----数据库多数据源组件【DataSourceAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("==> 【初始化--自动化配置】----数据库多数据源组件【DataSourceAutoConfiguration】");
    }
}
