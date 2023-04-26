#### mybatis-typehandlers-jsr310对Java日期对象和JDBC日期对象转换器过期

> 最近看到公司很多项目中都引入了这个依赖，但是都说不清楚到底有什么用，只知道是mybatis用来对日期做转换的，所以就研究一下到底做什么的；

-

mybatis-typehandlers-jsr310的GITHUB地址：[https://github.com/mybatis/typehandlers-jsr310](https://github.com/mybatis/typehandlers-jsr310)
-
mybatis-typehandlers-jsr310的Maven地址：[https://mvnrepository.com/artifact/org.mybatis/mybatis-typehandlers-jsr310](https://mvnrepository.com/artifact/org.mybatis/mybatis-typehandlers-jsr310)

一、mybatis-typehandlers-jsr310项目最新版本是1.0.2，是2017年发布的了，在官方GITHUB上也可以看到作者已经不在更新此项目，从mybatis3.4.5开始这个项目的所有代码都合并到了mybatis之中。所以使用3.4.5及以后的版本的都可以直接删除，无需引用此依赖；

二、对于直接依赖mybatis-spring-boot-starter的springboot项目通过其GITHUB地址[https://github.com/mybatis/spring-boot-starter/releases?page=2](https://github.com/mybatis/spring-boot-starter/releases?page=2)
上版本历史可以看到只要是1.2.2及之后的版本都可以直接删除此依赖引用；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

