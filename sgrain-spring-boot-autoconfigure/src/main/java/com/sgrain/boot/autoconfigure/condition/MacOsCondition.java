package com.sgrain.boot.autoconfigure.condition;


import com.sgrain.boot.common.utils.log.LoggerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @program: spring-parent
 * @description: condition条件注解判定类
 * @create: 2020/12/04
 */
public class MacOsCondition implements Condition {
    /**
     * 匹配操作系统类型
     * @param context 条件上线文
     * @param metadata 类或者方法的元数据
     * @return true 符合条件，false 不符合条件
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String osName = context.getEnvironment().getProperty("os.name");
        if (StringUtils.equalsIgnoreCase(osName, "Mac OS X")) {
            return true;
        }
        return false;
    }
}
