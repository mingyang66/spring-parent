### Spring AOP拦截控制器请求入参返回值时间两种方案
>在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理，如统计运行时间、拦截请求参数、获取返回值等等。

#### 1.概念
* @Aspect把当前类标识为一个切面供容器读取,有点类似java中的类声明，包含切点（Point Cut）、连接点（Joint Point）
* Joint Point（连接点）： 表示在程序中明确定义的点，如控制器中的一个个的方法
* Point Cut(切点)：表示一组的Joint Point(连接点)，这些连接点是通过一定的逻辑规则组合起来的，如当前示例通过切入点函数表达式将控制器方法组合起来这一类就叫做切点，一个个的控制器方法就是Joint Point(连接点)
* Advice(增强)：Advice定义了在Point Cut(切点)里面要做的事情，包括在切点Before、After、替换切点执行的代码模块

#### 2.切入点函数表达式配置规则
* 方法切入点函数：execution(<修饰符模式>? <返回类型模式> <方法名模式>(<参数模式>) <异常模式>?)  除了返回类型模式、方法名模式和参数模式外，其它项都是可选的
execution切入点表达式：
* 第一个*号：表示返回类型，*号表示所有的类型
* 包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包下的所有子包
* 第二个*号：表示类名，*号表示所有的类名
* 第三个*号：表示方法名，*号表示所有的方法，后面的括弧表示方法里面的参数，两个句点表示任意参数

annotation切入点表达式：
* 参数表示注解类，必须为全限定名
* 在多个表达式之间使用  || , or 表示  或 ，使用  && , and 表示  与 ， ！ 表示 非

|类别|函数|入参|说明|
|:---:|:---:|:---:|:---:|
|方法切点函数|execution()|方法匹配模式串|表示满足某一匹配模式的所有目标类方法连接点，如：execution(public * com.yaomy.control.test.api..*.*(..))|
|--|@annotation()|方法注解类名|表示标注了特定注解的目标方法连接点，如：@annotation(org.springframework.web.bind.annotation.GetMapping) or @annotation(org.springframework.web.bind.annotation.PostMapping)|

#### 3.切入点表达式demo示例一（还有二，往下看）
```
package com.yaomy.control.aop.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.common.control.conf.PropertyService;
import com.yaomy.control.common.control.utils.ObjectSizeUtil;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Aspect
public class ControllerAdvice {
    @Autowired
    private PropertyService propertyService;
    /**
     * 开始时间
     */
    private ThreadLocal<Long> startTime = new ThreadLocal<>();
    /**
     * 日志记录
     */
    private ThreadLocal<String> logApi = new ThreadLocal<>();

    private final String pCutStr = "execution(public * com.yaomy.control.test.api..*.*(..))";
    /**
     * @Description 定义切入点
     * @Version  1.0
     */
    @Pointcut(value = pCutStr)
    public void pointCut() {
    }
    /**
     * @Description 环绕增强（Advice），能控制切入点执行前、执行后，用这个注解后程序抛出异常会影响到@AfterThrowing注解
     * ProceedingJoinPoint提供proceed(..)方法用来支持切面Advice(增强)
     * @Version  1.0
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取连接点（Joint Point）的签名
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        ObjectMapper objectMapper = new ObjectMapper();
        //获取请求参数
        String reqParams = objectMapper.writeValueAsString(getReqestParam(joinPoint, methodSignature, request));
        //调用Joint Point(连接点)并返回处理结果
        Object result = joinPoint.proceed();
        //获取控制器类的Class对象
        Class declaringType = methodSignature.getDeclaringType();
        //获取控制器类上的注解
        Annotation[] annotations = declaringType.getAnnotations();
        //控制器存在并且控制器方法也存在
        if(isController(annotations)){
            String log = logApi.get();
            log = StringUtils.join(log, "\n");
            log = StringUtils.join(log, "控制器  ：" + methodSignature.toLongString(), "\n");
            log = StringUtils.join(log, "访问URL ："+request.getRequestURL(), "\n");
            log = StringUtils.join(log, "Method  ："+request.getMethod(), "\n");
            log = StringUtils.join(log, "请求参数："+reqParams, "\n");
            Long total = System.currentTimeMillis() - startTime.get();
            log = StringUtils.join(log,"耗  时  ：" + total + "ms");
            logApi.set(log);
        }
        return result;
    }
    /**
     * @Description 切入点方法执行之前执行，@Before是在Join point(连接点)之前执行的Advice(增强)
     * @Version  1.0
     */
    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
    }
    /**
     * @Description @After无论一个Joint Point(连接点)正常执行还是发生了异常都会执行的Advice(增强)
     * @Version  1.0
     */
    @After(value = "pointCut()")
    public void after(JoinPoint joinPoint) {
    }
    /**
     * @Description 切入点方法执行之后执行,@AfterReturning是在一个Join Point(连接点)正常返回后执行的Advice(增强)
     * @Version  1.0
     */
    @AfterReturning(value = "pointCut()", returning="returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        CodeSignature signature = (CodeSignature)joinPoint.getSignature();
        Class[] exceptionTypes = signature.getExceptionTypes();
        System.out.println(exceptionTypes.length);
        try {
            long total =  System.currentTimeMillis() - startTime.get();
            String log = logApi.get();
            log = StringUtils.join(log, ", 总耗时：" +total+"ms", "\n");
            ObjectMapper objectMapper = new ObjectMapper();
            if(null == returnValue){
                log = StringUtils.join(log, "返回结果：", "\n");
            } else if(returnValue instanceof ResponseEntity){
                log = StringUtils.join(log, "返回结果："+ objectMapper.writeValueAsString(((ResponseEntity)returnValue).getBody()), "\n");
            } else{
                log = StringUtils.join(log, "返回结果："+ objectMapper.writeValueAsString(returnValue), "\n");
            }
            log = StringUtils.join(log, "数据大小："+ ObjectSizeUtil.humanReadableUnits(returnValue));
            logApi.set(log);
            LoggerUtil.info(signature.getDeclaringType(), logApi.get());
        } catch (JsonProcessingException e){
            e.printStackTrace();
            LoggerUtil.error(signature.getDeclaringType(), e.toString());
        }
    }
    /**
     * @Description 判断当前类是否是控制器类型
     * @Version  1.0
     */
    private boolean isController(Annotation[] annotations){
        if(ArrayUtils.isEmpty(annotations)){
            return false;
        }
        boolean isController = false;
        for(Annotation annotation:annotations){
            if(annotation.annotationType().isAssignableFrom(RestController.class) || annotation.annotationType().isAssignableFrom(Controller.class)){
                   isController = true;
                   break;
            }
        }
        return isController;
    }
    /**
     * @Description 获取请求参数
     * @Version  1.0
     */
    private Map<String, Object> getReqestParam(ProceedingJoinPoint joinPoint, MethodSignature methodSignature, HttpServletRequest request){
        Map<String, Object> paramMap = new LinkedHashMap<>();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for(int i=0; i<parameterNames.length; i++){
            if(args[i] instanceof HttpServletRequest){
                Enumeration<String> params = request.getParameterNames();
                while (params.hasMoreElements()){
                    String key = params.nextElement();
                    paramMap.put(key, request.getParameter(key));
                }
            } else if(!(args[i] instanceof HttpServletResponse)){
                paramMap.put(parameterNames[i], args[i]);
            }
        }
        return paramMap;
    }
}
```

>上面这种是直接使用注解的方式来进行的，其实spring-aop已经封装了更简单的，下面直接看示例

#### 4.编写继承MethodInterceptor接口类（示例二）
```
package com.yaomy.control.aop.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.common.control.utils.ObjectSizeUtil;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description: 拦截URL请求
 * @Version: 1.0
 */
public class ControllerInterceptor implements MethodInterceptor {
    /**
     * 换行符
     */
    public static final String NEW_LINE = "\n";

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = invocation.proceed();
        stopWatch.stop();

        ObjectMapper objectMapper = new ObjectMapper();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String log = StringUtils.join(NEW_LINE, "控制器  ：", invocation.getThis().getClass(), ".", invocation.getMethod().getName(), NEW_LINE);
        log = StringUtils.join(log, "访问URL ：", request.getRequestURL(), NEW_LINE);
        log = StringUtils.join(log, "Method  ：", request.getMethod(), NEW_LINE);
        log = StringUtils.join(log, "请求参数：", getReqestParam(invocation), NEW_LINE);
        log = StringUtils.join(log,"耗  时  ：" , stopWatch.getTime(), "ms", NEW_LINE);
        if(ObjectUtils.isEmpty(result)){
            log = StringUtils.join(log, "返回结果：", result, NEW_LINE);
        } else if(result instanceof ResponseEntity){
            log = StringUtils.join(log, "返回结果：", objectMapper.writeValueAsString(((ResponseEntity)result).getBody()), NEW_LINE);
        } else {
            log = StringUtils.join(log, "返回结果：", objectMapper.writeValueAsString(result), NEW_LINE);
        }
        log = StringUtils.join(log, "数据大小：", ObjectSizeUtil.humanReadableUnits(result), NEW_LINE);
        LoggerUtil.info(invocation.getThis().getClass(), log);
        return result;
    }
    /**
     * @Description 获取请求参数
     * @Version  1.0
     */
    private Map<String, Object> getReqestParam(MethodInvocation invocation){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> paramMap = new LinkedHashMap<>();
            Object[] args = invocation.getArguments();
            Method method = invocation.getMethod();
            Parameter[] parameters = method.getParameters();
            if(ArrayUtils.isEmpty(parameters)){
                return null;
            }
            for(int i=0; i<parameters.length; i++){
                if(args[i] instanceof HttpServletRequest){
                    HttpServletRequest request = (HttpServletRequest) args[i];
                    Enumeration<String> params = request.getParameterNames();
                    while (params.hasMoreElements()){
                        String key = params.nextElement();
                        paramMap.put(key, request.getParameter(key));
                    }
                } else if(!(args[i] instanceof HttpServletResponse)){
                    paramMap.put(parameters[i].getName(), objectMapper.writeValueAsString(args[i]));
                }
            }
            return paramMap;
        } catch (JsonProcessingException e){
            return null;
        }
    }
}
```

#### 5.开启拦截器配置
```
@Configuration
public class ControllerConfig {
    @Autowired
    private Environment env;

    public static final String defaultPointCut = "execution(public * com.yaomy.control.test.api..*.*(..))";
    @Bean
    public DefaultPointcutAdvisor defaultPointCutAdvice() {
        ControllerInterceptor interceptor = new ControllerInterceptor();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(env.getProperty("spring.aop.control.expression", defaultPointCut));

        // 配置增强类advisor
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(pointcut);
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
```

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-aop-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-aop-service)