package com.emily.infrastructure.test.config;

import com.emily.infrastructure.common.UUIDUtils;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logback.entity.BaseLogger;
import com.emily.infrastructure.test.service.MysqlService;
import com.emily.infrastructure.tracing.holder.ContextWrapper;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
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

    //@Scheduled(fixedRate = 5000)
    public void doSchedule() {
        String traceId = UUIDUtils.randomSimpleUUID();
        System.out.println("父节点的ID是：" + traceId);
        ContextWrapper.run(null, () -> {
            BaseLogger.Builder builder = BaseLogger.newBuilder();
            builder.withTraceId(LocalContextHolder.current().getTraceId());
            builder.withSystemNumber("emis-schedule");
            System.out.println("start--------上下文-1-" + LocalContextHolder.current().getTraceId());
            mysqlService.getMysql();
            System.out.println("--------上下文-2-" + LocalContextHolder.current().getTraceId());
            mysqlService.getMysql();
            mysqlService.insertMysql();
            System.out.println("end--------上下文-3-" + LocalContextHolder.current().getTraceId());
            System.out.println(JsonUtils.toJSONString(builder.build()));
        }, traceId);
    }
}
