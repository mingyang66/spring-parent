### 基于springboot+mybatis+druid注解模式动态切换数据源，完全自动化配置模式

> 之前写过一篇基于springboot的多数据源自动化配置，但是这是一个半自动化配置模式，如果要加一个数据库则需要在配置类中将连接数据库配置手动硬编码模式写入代码，一直觉得这样做有点傻，如何做到只需新增配置就可以完成自动化配置呢？最近看源码终于有了方案，赶紧测试分享给大家。

之前的技术方案：[https://blog.csdn.net/yaomingyang/article/details/100807675](https://blog.csdn.net/yaomingyang/article/details/100807675)

##### 一、创建基于AbstractRoutingDataSource抽象类动态切换数据源的实现类

```java
package com.emily.infrastructure.datasource.context;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description: 抽象的数据源实现（javax.sql.DataSource），该实现基于查找键将getConnection()路由到各种目标数据源，目标数据源通常但是不限于通过一些线程绑定
 * 的事务上下文来确定
 * @Author Emily
 * @Version: 1.0
 */
public class DynamicMultipleDataSources extends AbstractRoutingDataSource {
    /**
     * 私有的构造函数
     * @param defaultTargetDataSource 默认数据源
     * @param targetDataSources 所有的数据源
     */
    private DynamicMultipleDataSources(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        /**
         * 如果存在默认数据源，指定默认的目标数据源；映射的值可以是javax.sql.DataSource或者是数据源（data source）字符串；
         * 如果setTargetDataSources指定的数据源不存在，将会使用默认的数据源
         */
        this.setDefaultTargetDataSource(defaultTargetDataSource);
        /**
         * 指定目标数据源的Map集合映射，使用查找键（Look Up Key）作为Key,这个Map集合的映射Value可以是javax.sql.DataSource或者是数据源（data source）字符串；
         * 集合的Key可以为任何数据类型，当前类会通过泛型的方式来实现查找，
         */
        this.setTargetDataSources(targetDataSources);
        /**
         * 指定对默认的数据源是否应用宽松的回退，如果找不到当前查找键（Look Up Key）的特定数据源，就返回默认的数据源，默认为true;
         */
        this.setLenientFallback(true);
        /**
         * 设置DataSourceLookup为解析数据源的字符串，默认是使用JndiDataSourceLookup；允许直接指定应用程序服务器数据源的JNDI名称；
         */
        this.setDataSourceLookup(null);
        /**
         * 将设置的默认数据源、目标数据源解析为真实的数据源对象赋值给resolvedDefaultDataSource变量和resolvedDataSources变量
         */
        this.afterPropertiesSet();
    }

    /**
     * 构件DynamicDataSource对象静态方法
     */
    public static DynamicMultipleDataSources build(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        return new DynamicMultipleDataSources(defaultTargetDataSource, targetDataSources);
    }

    /**
     * 确定当前线程的查找键，这通常用于检查线程绑定事物的上下文，允许是任意的键（Look Up Key），
     * 返回的查找键（Look Up Key）需要与存储的查找键（Look Up key）类型匹配
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSource();
    }

}

```

##### 二、创建持有当前线程上下文数据源标识类

```java
package com.emily.infrastructure.datasource.context;

/**
 * @Description: 线程持有数据源上线文
 * @Author Emily
 * @Version: 1.0
 */
public class DataSourceContextHolder {
    /**
     * 当前线程对应的数据源
     */
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    /**
     * 设置当前线程持有的数据源
     */
    public static void setDataSource(String dataSource) {
        CONTEXT.set(dataSource);
    }

    /**
     * 获取当前线程持有的数据源
     */
    public static String getDataSource() {
        return CONTEXT.get();
    }

    /**
     * 删除当前线程持有的数据源
     */
    public static void clearDataSource() {
        CONTEXT.remove();
    }

}

```

##### 三、创建注解类，通过此注解标注方法，然后通过切面可以实现动态切换数据源

```java
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {
    String value() default DataSourceProperties.DEFAULT_CONFIG;
}
```

##### 四、创建一个AOP切面类，根据注解标注的数据源标识动态的切换数据源

```java
package com.emily.infrastructure.datasource.interceptor;

import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.datasource.DataSourceProperties;
import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.context.DataSourceContextHolder;
import com.emily.infrastructure.autoconfigure.logger.common.LoggerUtils;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Author Emily
 * @Version: 1.0
 */
public class DataSourceMethodInterceptor implements MethodInterceptor {

    private DataSourceProperties dataSourceProperties;

    public DataSourceMethodInterceptor(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //数据源切换开始
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        //获取注解标注的数据源
        String dataSource = targetDataSource.value();
        //判断当前的数据源是否已经被加载进入到系统当中去
        if (!dataSourceProperties.getConfig().containsKey(dataSource)) {
            throw new NullPointerException(String.format("数据源配置【%s】不存在", dataSource));
        }
        try {
            LoggerUtils.info(method.getDeclaringClass(), StringUtils.join("==》", method.getDeclaringClass().getName(), ".", method.getName(), String.format("========开始执行，切换数据源到【%s】========", dataSource)));
            //切换到指定的数据源
            DataSourceContextHolder.setDataSource(dataSource);
            //调用TargetDataSource标记的切换数据源方法
            Object result = invocation.proceed();
            return result;
        } catch (Throwable ex) {
            LoggerUtils.error(invocation.getThis().getClass(), String.format("==》========异常执行，数据源【%s】 ========" + PrintExceptionInfo.printErrorInfo(ex), dataSource));
            throw ex;
        } finally {
            //移除当前线程对应的数据源
            DataSourceContextHolder.clearDataSource();
            LoggerUtils.info(method.getDeclaringClass(), StringUtils.join("==》", method.getDeclaringClass().getName(), ".", method.getName(), String.format("========结束执行，清除数据源【%s】========", dataSource)));

        }
    }

}

```

##### 五、创建一个自动化属性配置类，多个数据源可以按照配置类指定的模式配置

```java
package com.emily.infrastructure.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 数据源配置文件
 * @author: Emily
 * @create: 2020/05/14
 */
@ConfigurationProperties(prefix = "spring.emily.datasource")
public class DataSourceProperties {
    /**
     * 默认配置
     */
    public static final String DEFAULT_CONFIG = "default";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 默认配置
     */
    private String defaultConfig = DEFAULT_CONFIG;
    /**
     * 多数据源配置
     */
    private Map<String, DruidDataSource> config = new HashMap<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(String defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Map<String, DruidDataSource> getConfig() {
        return config;
    }

    public void setConfig(Map<String, DruidDataSource> config) {
        this.config = config;
    }

    public DruidDataSource getDefaultDataSource() {
        return this.config.get(this.getDefaultConfig());
    }
}

```

##### 六、创建一个自动化配置类，IOC容器启动时自动的将属性配置中数据库配置注入到多数据源对象

```java
package com.emily.infrastructure.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.emily.infrastructure.common.enums.AopOrderEnum;
import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.autoconfigure.logger.common.LoggerUtils;
import com.emily.infrastructure.datasource.context.DynamicMultipleDataSources;
import com.emily.infrastructure.datasource.interceptor.DataSourceMethodInterceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String DEFAULT_POINT_CUT = "@annotation(com.emily.infrastructure.datasource.annotation.TargetDataSource)";

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
     * 从配置文件中获取多数据源配置信息
     * {@link DataSourceTransactionManagerAutoConfiguration}
     * {@link MybatisAutoConfiguration}
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
        configs.keySet().forEach(key -> targetDataSources.put(key, configs.get(key)));
        return DynamicMultipleDataSources.build(dataSourceProperties.getDefaultDataSource(), targetDataSources);
    }

    @Override
    public void destroy() {
        LoggerUtils.info(DataSourceAutoConfiguration.class, "==》【销毁--自动化配置】----数据库多数据源组件【DataSourceAutoConfiguration】");
    }

    @Override
    public void afterPropertiesSet() {
        LoggerUtils.info(DataSourceAutoConfiguration.class, "==》【初始化--自动化配置】----数据库多数据源组件【DataSourceAutoConfiguration】");
    }
}

```

##### 七、数据源配置类示例

```yaml
spring:
  emily:
    datasource:
      config:
        default:
          driver-class-name: oracle.jdbc.OracleDriver
          url: jdbc:oracle:thin:@xx.xx.xx:xx:xx
          username: xx
          password: xx
          type: com.alibaba.druid.pool.DruidDataSource
        slave:
          driver-class-name: oracle.jdbc.OracleDriver
          url: jdbc:oracle:thin:@xx.xx.xx:xx:xx
          username: xx
          password: xx
          type: com.alibaba.druid.pool.DruidDataSource
        mysql:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://127.0.0.1:3306/sgrain?characterEncoding=utf-8
          username: xx
          password: x
          type: com.alibaba.druid.pool.DruidDataSource
        sqlserver:
          driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
          url: jdbc:sqlserver://xx.xx.xx:xx;databaseName=xx
          username: x
          password: xx
          type: com.alibaba.druid.pool.DruidDataSource
```

> 经过上述多步我们实现了一个基于springboot自动化配置的多数据源切换组件，支持多种数据库，经测试支持Oracle、SqlServer、Mysql、TIDB等数据库。

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

