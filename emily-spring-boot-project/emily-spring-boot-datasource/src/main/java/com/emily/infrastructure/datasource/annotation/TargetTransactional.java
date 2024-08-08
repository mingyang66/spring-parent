package com.emily.infrastructure.datasource.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * 目标事务
 * 技能点总结
 * 1.@AliasFor 标注：1.注解内别名，2.元数据的别名
 * 2.事务失效
 * 场景一：如果方法中有try{}catch(Exception ex){}处理，那么try里面的代码就脱离了事物的管理，若要事务生效需要在catch中throw new Exception；
 * 场景二：类内部通过this访问的方法上标注@Transactional事务不生效，因为AOP是基于代理类调用，this调用是真实类调用；
 * 场景三：私有方法上添加@Transactional注解不生效
 * 场景四：@Transactional未设置rollbackFor=Exception.class属性，即：@Transactional注解默认只处理运行时异常，也就是只有抛出运行时异常才会触发事务回滚，否则不会回滚；
 * 场景五：父线程抛异常，事务回滚；子线程不抛异常，因为子线程是独立存在，和父线程不在同一个事务，所以子线程的修改并不会被回滚；父线程不抛出异常，子线程抛出异常，由于子线程异常不会
 * 被外部线程捕获，所以父线程不抛异常，事务回滚不生效；
 * 参考资料：https://mp.weixin.qq.com/s/r388pF8-c6sPVyVGjLBjqg
 * 核心代码：TransactionInterceptor
 *
 * @author Emily
 * @since :  Created in 2022/7/16 1:38 下午
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TargetDataSource
@Transactional
public @interface TargetTransactional {
    /**
     * 指定要切换的数据库标识，默认是：default
     *
     * @return 要切换的数据库标识
     */
    @AliasFor(annotation = TargetDataSource.class, value = "value")
    String value() default "";

    /**
     * 事务管理器，默认根据value指定的数据源获取事务管理器
     *
     * @return 事务管理器
     */
    @AliasFor(annotation = Transactional.class, value = "transactionManager")
    String transactionManager() default "";

    @AliasFor(annotation = Transactional.class, value = "label")
    String[] label() default {};

    /**
     * 事务的传播行为
     * org.springframework.transaction.annotation.Propagation#REQUIRED：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事物；这是默认事值。
     * org.springframework.transaction.annotation.Propagation#REQUIRES_NEW：创建一个新的事物，如果当前存在事务，则把当前事物挂起。
     * org.springframework.transaction.annotation.Propagation#SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行。
     * org.springframework.transaction.annotation.Propagation#NOT_SUPPORTED：以非事务方式运行，如果当前存在事务，则把当前事物挂起。
     * org.springframework.transaction.annotation.Propagation#NEVER：以非事务方式运行，如果当前存在事务，则抛出异常。
     * org.springframework.transaction.annotation.Propagation#MANDATORY：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。
     * org.springframework.transaction.annotation.Propagation#NESTED：如果当前存在事务，则创建一个事物作为当前事物的嵌套事务来运行，如果当前没有事务，则取值等价于Propagation#REQUIRED
     *
     * @return 事务传播行为
     */
    @AliasFor(annotation = Transactional.class, value = "propagation")
    Propagation propagation() default Propagation.REQUIRED;

    /**
     * 事务隔离级别
     *
     * @return 隔离级别对象
     */
    @AliasFor(annotation = Transactional.class, value = "isolation")
    Isolation isolation() default Isolation.DEFAULT;

    @AliasFor(annotation = Transactional.class, value = "timeout")
    int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

    @AliasFor(annotation = Transactional.class, value = "timeoutString")
    String timeoutString() default "";

    /**
     * 指定事务实际上是否只读
     *
     * @return 是否只读
     */
    @AliasFor(annotation = Transactional.class, value = "readOnly")
    boolean readOnly() default false;

    /**
     * 默认指定0或者多个异常类，这些类必须是Throwable的子类，指定哪些类可以触发事务回滚；
     * 默认情况事务只会捕获RuntimeException和Error异常时触发回滚机制（非受检异常）；
     * 如果未指定受检异常（业务异常）则不会触发回滚机制
     *
     * @return 抛出的异常对象
     */
    @AliasFor(annotation = Transactional.class, value = "rollbackFor")
    Class<? extends Throwable>[] rollbackFor() default {};

    @AliasFor(annotation = Transactional.class, value = "rollbackForClassName")
    String[] rollbackForClassName() default {};

    @AliasFor(annotation = Transactional.class, value = "noRollbackFor")
    Class<? extends Throwable>[] noRollbackFor() default {};

    @AliasFor(annotation = Transactional.class, value = "noRollbackForClassName")
    String[] noRollbackForClassName() default {};
}
