package com.sgrain.boot.autoconfigure.aop.idempotent;

import com.sgrain.boot.autoconfigure.aop.interceptor.IdempotentMethodInterceptor;
import com.sgrain.boot.common.enums.AopOrderEnum;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import com.sgrain.boot.common.utils.UUIDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 接口被指定的客户端调用频率限制自动化配置
 * @create: 2020/03/23
 */
@Configuration
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.idempotent", name = "enable", havingValue = "true", matchIfMissing = true)
@RestController
public class IdempotentAutoConfiguration {
    /**
     * 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非
     */
    private static final String REPEAT_SUBMIT_POINT_CUT = StringUtils.join("@annotation(com.sgrain.boot.autoconfigure.aop.annotation.Idempotent)");

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 控制器AOP拦截处理
     */
    @Bean
    public DefaultPointcutAdvisor repeatSubmitPointCutAdvice() {
        //声明一个AspectJ切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        //设置切点表达式
        pointcut.setExpression(REPEAT_SUBMIT_POINT_CUT);
        // 配置增强类advisor, 切面=切点+增强
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        //设置切点
        advisor.setPointcut(pointcut);
        //设置增强（Advice）
        advisor.setAdvice(new IdempotentMethodInterceptor(redisTemplate));
        //设置增强拦截器执行顺序
        advisor.setOrder(AopOrderEnum.IDEMPOTENT.getOrder());

        return advisor;
    }
    /**
     * 自动生成token令牌，并将令牌存入缓存，过期时间是30s
     */
    @GetMapping("token/generation")
    public ResponseEntity<String> generationToken(){
        String token = UUIDUtils.generation();
        redisTemplate.opsForValue().set(StringUtils.join("idempotent", CharacterUtils.COLON_EN, token), token, 30, TimeUnit.SECONDS);
        return ResponseEntity.ok(token);
    }
}
