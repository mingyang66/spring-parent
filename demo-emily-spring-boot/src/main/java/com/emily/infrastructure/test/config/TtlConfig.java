package com.emily.infrastructure.test.config;

import com.emily.infrastructure.core.context.holder.ContextWrapper;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;
import com.emily.infrastructure.core.entity.BaseLogger;
import com.emily.infrastructure.core.entity.BaseLoggerBuilder;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.test.mapper.mysql.MysqlMapper;
import com.emily.infrastructure.test.service.MysqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * TTL自定义修饰测试
 *
 * @author :  Emily
 * @since :  2023/8/11 2:06 PM
 */
@EnableScheduling
@Component
public class TtlConfig {
    @Autowired
    private MysqlService mysqlService;

    @Scheduled(fixedRate = 5000)
    public void doSchedule() {
        ContextWrapper.run(() -> {
            BaseLoggerBuilder builder = BaseLoggerBuilder.create();
            builder.withTraceId(LocalContextHolder.current().getTraceId());
            builder.withSystemNumber("emis-schedule");
            System.out.println("start--------上下文-1-" + LocalContextHolder.current().getTraceId());
            mysqlService.getMysql();
            System.out.println("--------上下文-2-" + LocalContextHolder.current().getTraceId());
            mysqlService.getMysql();
            mysqlService.insertMysql();
            System.out.println("end--------上下文-3-" + LocalContextHolder.current().getTraceId());
            System.out.println(JsonUtils.toJSONString(builder.build()));
        });
    }
}
