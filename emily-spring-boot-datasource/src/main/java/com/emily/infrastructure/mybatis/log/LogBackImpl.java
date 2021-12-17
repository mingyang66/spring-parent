package com.emily.infrastructure.mybatis.log;

import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.apache.ibatis.logging.Log;
import org.slf4j.Logger;

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
        LoggerFactory.getModuleLogger("database", "database").error(s);
        e.printStackTrace(System.err);
    }

    @Override
    public void error(String s) {
        LoggerFactory.getModuleLogger("database", "database").error(s);
    }

    @Override
    public void debug(String s) {
        LoggerFactory.getModuleLogger("database", "database").debug(s);
    }

    @Override
    public void trace(String s) {
        LoggerFactory.getModuleLogger("database", "database").trace(s);
    }

    @Override
    public void warn(String s) {
        LoggerFactory.getModuleLogger("database", "database").warn(s);
    }
}
