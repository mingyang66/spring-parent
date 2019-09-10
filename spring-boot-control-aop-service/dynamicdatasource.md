### Spring boot+mybatis+druid注解模式动态切换多数据源

>通常情况下一个项目里面只连接一个数据库就可以，但是也有很多种情况需要配置多个数据源的场景，本篇就讲解下使用spring boot、mybatis、druid配置多数据源的方式。

#### 1.在pom文件中引入需要的依赖
```
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.20</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>cn.easyproject</groupId>
            <artifactId>ojdbc7</artifactId>
            <version>12.1.0.2.0</version>
        </dependency>
 ```
 
 #### 2.创建一个持有数据源上下文的DataSourceContextHolder类
 ```
 package com.yaomy.control.aop.datasource;
 
 import org.apache.commons.lang3.StringUtils;
 import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
 
 import javax.sql.DataSource;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * @Description: 线程持有数据源上线文
  * @Author yaomy
  * @Version: 1.0
  */
 public class DataSourceContextHolder  {
     /**
      * 当前线程对应的数据源
      */
     private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
     /**
      * 存储当前系统加载的数据源的查找键（look up key）KEY
      */
     public static final Set<Object> ALL_DATA_SOURCE_KEY = new HashSet<>();
 
     /**
      * 设置当前线程持有的数据源
      */
     public static void setDataSource(String dataSource){
         if(isExist(dataSource)){
             CONTEXT_HOLDER.set(dataSource);
         } else {
             throw new NullPointerException(StringUtils.join("数据源查找键（Look up key）【", dataSource,"】不存在"));
         }
     }
     /**
      * 获取当前线程持有的数据源
      */
     public static String getDataSource(){
         return CONTEXT_HOLDER.get();
     }
 
     /**
      * 删除当前线程持有的数据源
      */
     public static void remove(){
         CONTEXT_HOLDER.remove();
     }
 
     /**
      * 判断数据源在系统中是否存在
      */
     public static boolean isExist(String dataSource){
         if(StringUtils.isEmpty(dataSource)){
             return false;
         }
         if(ALL_DATA_SOURCE_KEY.contains(dataSource)){
             return true;
         }
         return false;
     }
 }
```
>为了线程的安全使用ThreadLocal来存储数据源查找键（Look Up key）

#### 3.多数据源实现，实现类需要继承AbstractRoutingDataSource抽象类,实现通过查找键动态的切换数据源
```
package com.yaomy.control.aop.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * @Description: 抽象的数据源实现（javax.sql.DataSource），该实现基于查找键将getConnection()路由到各种目标数据源，目标数据源通常但是不限于通过一些线程绑定
 * 的事务上下文来确定
 * @Author yaomy
 * @Version: 1.0
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    /**
     * 私有的构造函数
     * @param defaultTargetDataSource 默认数据源
     * @param targetDataSources 所有的数据源
     */
    private DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        /**
         * 如果存在默认数据源，指定默认的目标数据源；映射的值可以是javax.sql.DataSource或者是数据源（data source）字符串；
         * 如果setTargetDataSources指定的数据源不存在，将会使用默认的数据源
         */
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        /**
         * 指定目标数据源的Map集合映射，使用查找键（Look Up Key）作为Key,这个Map集合的映射Value可以是javax.sql.DataSource或者是数据源（data source）字符串；
         * 集合的Key可以为任何数据类型，当前类会通过泛型的方式来实现查找，
         */
        super.setTargetDataSources(targetDataSources);
        /**
         * 指定对默认的数据源是否应用宽松的回退，如果找不到当前查找键（Look Up Key）的特定数据源，就返回默认的数据源，默认为true;
         */
        super.setLenientFallback(true);
        /**
         * 设置DataSourceLookup为解析数据源的字符串，默认是使用JndiDataSourceLookup；允许直接指定应用程序服务器数据源的JNDI名称；
         */
        super.setDataSourceLookup(null);
        /**
         * 将设置的默认数据源、目标数据源解析为真实的数据源对象赋值给resolvedDefaultDataSource变量和resolvedDataSources变量
         */
        super.afterPropertiesSet();
        /**
         * 将数据源查找键（look up key）KEY存储进入静态变量中，供其它地方校验使用
         */
        DataSourceContextHolder.ALL_DATA_SOURCE_KEY.addAll(targetDataSources.keySet());
    }

    /**
     * 构件DynamicDataSource对象静态方法
     */
    public static DynamicDataSource build(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources){
        return new DynamicDataSource(defaultTargetDataSource, targetDataSources);
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

#### 4.定义动态切换的注解类TargetDataSource
```
package com.yaomy.control.aop.annotation;

import com.yaomy.control.aop.constant.DbType;

import java.lang.annotation.*;

/**
 * @Description: 自定义注解，切换数据源,默认主数据源primary
 * @Version: 1.0
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {
    String value() default DbType.DEFAULT_DATASOURCE;
}
```

#### 5.上面几步把基础已经搭建完成了，这一步通过spring boot自带AOP接口MethodInterceptor进行动态的切换数据源
```
package com.yaomy.control.aop.advice;

...

/**
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
@Component
public class ControllerAdviceInterceptor implements MethodInterceptor {
    ...

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        //数据源切换aop处理
        return dataSourceHandler(invocation);
      
    }
    /**
     * 数据源切换AOP拦截处理
     */
    private Object dataSourceHandler(MethodInvocation invocation) throws Throwable{
        //获取Method对象
        Method method = invocation.getMethod();
        //数据源切换开始
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        //获取注解标注的数据源
        String dataSource = targetDataSource.value();
        //判断当前的数据源是否已经被加载进入到系统当中去
        if(!DataSourceContextHolder.isExist(dataSource)){
            throw new NullPointerException(StringUtils.join("数据源查找键（Look up key）【", dataSource,"】不存在"));
        }
        try{
            LoggerUtil.info(invocation.getThis().getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_START, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));
            //切换到指定的数据源
            DataSourceContextHolder.setDataSource(dataSource);
            //调用TargetDataSource标记的切换数据源方法
            Object result = invocation.proceed();
            //移除当前线程对应的数据源
            DataSourceContextHolder.remove();
            LoggerUtil.info(invocation.getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_END, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));

            return result;
        } catch (Throwable e){
            //移除当前线程对应的数据源
            DataSourceContextHolder.remove();
            LoggerUtil.error(invocation.getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_END, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));
            throw new Throwable(e);
        }
    }

}
```