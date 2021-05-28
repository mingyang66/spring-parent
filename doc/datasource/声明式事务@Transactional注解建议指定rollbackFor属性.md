### 声明式事务@Transactional注解建议指定rollbackFor属性

> Throwable是一个顶层的异常类，有Error和Exception两个子类，Error标识严重的错误；Exception可分为运行时异常（RuntimeException及其子类）和非运行时异常（Exception的非RuntimeException及其子类异常）

下图是我对事务异常回滚的一个归纳总结：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20210528144444903.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)

Spring的@Transactional注解可以很方便的开启事务，但事务默认只在遇到Error和运行时异常时才会回滚，在非运行时异常时是不会发生回滚；所以大家都养成好习惯统一@Transactional(rollbackFor = Exception.class)写法；

GitHub地址:[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

