package com.emily.infrastructure.datasource.log;

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
