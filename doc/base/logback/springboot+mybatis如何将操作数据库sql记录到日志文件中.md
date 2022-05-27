### springboot+mybatis如何将操作数据库sql记录到日志文件中

> 通常我们可以通过如下配置将操作数据库的sql语句打印到控制台上，但是如何将这些sql语句记录到日志文件中方便我们查询问题呢？

```yaml
mybatis:
  # 标注待解析的mapper的xml文件位置
  mapper-locations: classpath:mapper/*.xml
  configuration:
    # org.apache.ibatis.logging.slf4j.Slf4jImpl
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

##### 具体实现方式是重写StdOutImpl类，实现方式如下：

```java
package com.emily.infrastructure.mybatis.log;

import com.emily.infrastructure.logback.factory.LogbackFactory;
import org.apache.ibatis.logging.Log;

/**
* @Description: 将mybatis sql语句记录到日志文件中实现类，是org.apache.ibatis.logging.stdout.StdOutImpl类的替换
* @Author: Emily
* @create: 2021/8/22
*/
public class LogBackImpl implements Log {
    public LogBackImpl(String clazz) {
        // Do Nothing
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public void error(String s, Throwable e) {
        LogbackFactory.module("database", "database", s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        LogbackFactory.module("database", "database", s);
    }

    @Override
    public void debug(String s) {
        LogbackFactory.module("database", "database", s);
    }

    @Override
    public void trace(String s) {
        LogbackFactory.module("database", "database", s);
    }

    @Override
    public void warn(String s) {
        LogbackFactory.module("database", "database", s);
    }
}

```

##### 要想重写的实现类生效，需将配置替换为实现类，如下：

```
mybatis:
  # 标注待解析的mapper的xml文件位置
  mapper-locations: classpath:mapper/*.xml
  configuration:
    # org.apache.ibatis.logging.slf4j.Slf4jImpl
    log-impl: com.emily.infrastructure.datasource.log.LogBackImpl
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

