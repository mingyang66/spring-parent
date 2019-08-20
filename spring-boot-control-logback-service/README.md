### Spring boot logback日志系统集成

Spring boot默认使用的日志就是logback，我们就以logback为基础搭建公用的log系统，并将其发不成为第三方jar包给其它的系统使用。
参考网址：
[http://logback.qos.ch/manual/appenders.html#ConsoleAppender](http://logback.qos.ch/manual/appenders.html#ConsoleAppender)
[http://logback.qos.ch/manual/filters.html#thresholdFilter](http://logback.qos.ch/manual/filters.html#thresholdFilter)

#### 1.配置日志xml文件logback-control.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE >ALL -->
<configuration>
    <!-- name的值是变量的名称，value的值时变量定义的值。通过定义的值会被插入到logger上下文中。定义变量后，可以使“${}”来使用变量。 -->
    <property name="log.path" value="./logs" />
    <!--日志保存的有效时间（天）-->
    <property name="log.date" value="30" />
    <property name="pattern" value="[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-10.10thread] [%-5level] [%-36.36logger{36}:%-4.4line] : %msg%n"></property>
    <property name="patternMsg" value="%msg%n"></property>
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <!-- 日志过滤级别 -->
            <level>INFO</level>
        </filter>
        <encoder>
            <!-- 格式化输出 -->
            <pattern>${pattern}</pattern>
            <!-- 输出编码 -->
            <charset>utf8</charset>
        </encoder>
    </appender>
    <!-- INFO日志文件输出 -->
    <appender name="INFO" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!-- 日志过滤级别 -->
            <level>INFO</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <!-- 格式化输出 -->
            <pattern>${pattern}</pattern>
            <!-- 输出编码 -->
            <charset>utf8</charset>
        </encoder>
        <!-- 文件存放路径 -->
        <file>${log.path}/info/info.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天归档 -->
            <fileNamePattern>${log.path}/info/info.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志存放周期（天） -->
            <maxHistory>${log.date}</maxHistory>
        </rollingPolicy>
    </appender>
    <!-- TRACE日志文件输出 -->
    <appender name="TRACE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="com.yaomy.log.filter.EnhanceLevelFilter">
            <!-- 日志过滤级别 -->
            <level>TRACE</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <!-- 格式化输出 -->
            <pattern>${patternMsg}</pattern>
            <!-- 输出编码 -->
            <charset>utf8</charset>
        </encoder>
        <!-- 文件存放路径 -->
        <file>${log.path}/user/user_action.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天归档 -->
            <fileNamePattern>${log.path}/user/user_action.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志存放周期（天） -->
            <maxHistory>${log.date}</maxHistory>
        </rollingPolicy>
    </appender>
    <!-- ERROR日志文件输出 -->
    <appender name="ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!-- 日志过滤级别 -->
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <!-- 格式化输出 -->
            <pattern>${pattern}</pattern>
            <!-- 输出编码 -->
            <charset>utf8</charset>
        </encoder>
        <!-- 文件存放路径 -->
        <file>${log.path}/error/error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天归档 -->
            <fileNamePattern>${log.path}/error/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志存放周期（天） -->
            <maxHistory>${log.date}</maxHistory>
        </rollingPolicy>
    </appender>
    <!-- WARN日志文件输出 -->
    <appender name="WARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!-- 日志过滤级别 -->
            <level>WARN</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <!-- 格式化输出 -->
            <pattern>${pattern}</pattern>
            <!-- 输出编码 -->
            <charset>utf8</charset>
        </encoder>
        <!-- 文件存放路径 -->
        <file>${log.path}/warn/warn.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天归档 -->
            <fileNamePattern>${log.path}/warn/warn.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志存放周期（天） -->
            <maxHistory>${log.date}</maxHistory>
        </rollingPolicy>
    </appender>
    <!-- DEBUG日志文件输出 -->
    <appender name="DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <!-- 日志过滤级别 -->
            <level>DEBUG</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <!-- 格式化输出 -->
            <pattern>${pattern}</pattern>
            <!-- 输出编码 -->
            <charset>utf8</charset>
        </encoder>
        <!-- 文件存放路径 -->
        <file>${log.path}/debug/debug.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- 每天归档 -->
            <fileNamePattern>${log.path}/debug/debug.%d{yyyy-MM-dd}.log</fileNamePattern>
            <!-- 日志存放周期（天） -->
            <maxHistory>7</maxHistory>
        </rollingPolicy>
    </appender>
    <!-- 基础日志输出级别,大于等于level,不过跟filter中的是冲突的，只有一个生效，默认filter优先级更高 -->
    <root level="TRACE">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ERROR"/>
        <appender-ref ref="WARN"/>
        <appender-ref ref="INFO"/>
        <appender-ref ref="DEBUG"/>
        <appender-ref ref="TRACE"/>
    </root>
</configuration>
```
>上面的日志配置输出五种日志文件，并且日志文件每天自动滚动成一个文件，可以控制文件保存的有效期；其中输出控制台的日志文件使用的过滤器是阈值过滤器ThresholdFilter，
其它的使用的是级别过滤器LevelFilter，有一种特殊的日志TRACE使用的是自定义的过滤器EnhanceLevelFilter，主要是用来生成用户自定义的JSON格式日志文件

#### 2.看下自定义过滤器EnhanceLevelFilter
```
package com.yaomy.log.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

public class EnhanceLevelFilter extends LevelFilter {

    Level level;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith("{")
                && event.getMessage().endsWith("}")
                && event.getLevel().equals(level)) {
            return FilterReply.ACCEPT;
        } else {
            return FilterReply.DENY;
        }
    }
    @Override
    public void setLevel(Level level) {
        this.level = level;
    }
    @Override
    public void start() {
        if (this.level != null) {
            super.start();
        }
    }
}
```
>自定义过滤器是LevelFilter的一个子类，是为了满足特殊需求定义的

#### 3.接下来看下我们封装的工具类
```
package com.yaomy.log.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.log.po.UserAction;
import org.slf4j.LoggerFactory;

/**
 * @Description: 日志工具类 日志级别总共有TARCE < DEBUG < INFO < WARN < ERROR < FATAL，且级别是逐渐提供，
 * 如果日志级别设置为INFO，则意味TRACE和DEBUG级别的日志都看不到。
 * @Version: 1.0
 */
public class LoggerUtil {

    public static <T> void info(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).info(msg);
    }

    public static <T> void warn(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).warn(msg);
    }

    public static <T> void debug(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).debug(msg);
    }

    public static <T> void error(Class<T> clazz, String msg){
        LoggerFactory.getLogger(clazz).error(msg);
    }

    public static void user(UserAction userAction){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            LoggerFactory.getLogger(LoggerUtil.class).trace(objectMapper.writeValueAsString(userAction));
        } catch (JsonProcessingException e){
            error(LoggerUtil.class, e.toString());
            System.out.println("----------");
        }
    }

}
```
#### 4.自定义的UserAction类
```
package com.yaomy.log.po;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
public class UserAction implements Serializable {
    //重命名
    @JsonProperty("NUMBER")
    private String number;
    @JsonProperty("USERNAME")
    private String username;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```
#### 5.测试日志
```
package com.yaomy.log;


import com.yaomy.log.po.UserAction;
import com.yaomy.log.utils.LoggerUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(LogBootStrap.class, args);
        LoggerUtil.info(LogBootStrap.class,"sfddsf-----------------");
        LoggerUtil.info(LogBootStrap.class,"{username:liming}");
        LoggerUtil.error(LogBootStrap.class,"-----------error------------");
        LoggerUtil.warn(LogBootStrap.class,"----------warn--------------");
        LoggerUtil.debug(LogBootStrap.class,"---------------debug-");
        UserAction userAction = new UserAction();
        userAction.setUsername("dsdf");
        userAction.setNumber("12");
        LoggerUtil.user(userAction);
    }
}
```
>日志配置好之后就可以单独的将其达成jar包给其它系统来使用，也可以给其它的module使用

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-logback-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-logback-service)