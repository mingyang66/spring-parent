package com.yaomy.control.aop.advice;

import com.yaomy.control.aop.annotation.TargetDataSource;
import com.yaomy.control.aop.datasource.DataSourceContextHolder;
import com.yaomy.control.common.control.po.BaseRequest;
import com.yaomy.control.common.control.utils.JSONUtils;
import com.yaomy.control.common.control.utils.ObjectSizeUtil;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
 * @Description: 在接口到达具体的目标即控制器方法之前获取方法的调用权限，可以在接口方法之前或者之后做Advice(增强)处理
 * @Version: 1.0
 */
@Component
public class ControllerAdviceInterceptor implements MethodInterceptor {
    /**
     * 换行符
     */
    private static final String NEW_LINE = "\n";
    /**
     * 毫秒
     */
    private static final String MILLI_SECOND = "ms";
    /**
     * 控制器
     */
    private static final String MSG_CONTROLLER = "类|方法  ：";
    /**
     * 访问URL
     */
    private static final String MSG_ACCESS_URL = "访问URL ：";
    /**
     * 请求Method
     */
    public static final String MSG_METHOD = "Method  ：";
    /**
     * 请求PARAM
     */
    private static final String MSG_PARAMS = "请求参数：";
    /**
     * 耗时
     */
    private static final String MSG_TIME= "耗  时  ：";
    /**
     * 返回结果
     */
    private static final String MSG_RETURN_VALUE = "返回结果：";
    /**
     * 数据大小
     */
    private static final String MSG_DATA_SIZE = "数据大小：";
    /**
     * 异常
     */
    private static final String MSG_EXCEPTION = "异  常  ：";
    /**
     * START消息
     */
    private static final String MSG_DATASOURCE_START = "开始执行，切换数据源到【";
    /**
     * END消息
     */
    private static final String MSG_DATASOURCE_END = "执行结束，移除数据源【";
    /**
     * 中文右符号
     */
    private static final String MSG_RIGHT_SYMBOL = "】";


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        if(method.isAnnotationPresent(TargetDataSource.class)){
            //数据源切换aop处理
            return dataSourceHandler(invocation);
        } else {
            //控制器请求aop处理
            return controllerHandler(invocation);
        }
    }
    /**
     * 数据源切换AOP拦截处理
     */
    private Object dataSourceHandler(MethodInvocation invocation) throws Throwable{
        //获取Method对象
        Method method = invocation.getMethod();
        //数据源切换开始
        TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
        //获取注解标注的数据源
        String dataSource = targetDataSource.value();
        //判断当前的数据源是否已经被加载进入到系统当中去
        if(!DataSourceContextHolder.isExist(dataSource)){
            throw new NullPointerException(StringUtils.join("数据源查找键（Look up key）【", dataSource,"】不存在"));
        }
        try{
            LoggerUtil.info(invocation.getThis().getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_START, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));
            //切换到指定的数据源
            DataSourceContextHolder.setDataSource(dataSource);
            //调用TargetDataSource标记的切换数据源方法
            Object result = invocation.proceed();
            //移除当前线程对应的数据源
            DataSourceContextHolder.remove();
            LoggerUtil.info(invocation.getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_END, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));

            return result;
        } catch (Throwable e){
            //移除当前线程对应的数据源
            DataSourceContextHolder.remove();
            LoggerUtil.error(invocation.getClass(), StringUtils.join(MSG_CONTROLLER, invocation.getThis().getClass(), ".", method.getName(), MSG_DATASOURCE_END, dataSource, MSG_RIGHT_SYMBOL, NEW_LINE));
            throw new Throwable(e);
        }
    }
    /**
     *  控制器请求AOP拦截处理
     */
    private Object controllerHandler(MethodInvocation invocation) throws Throwable{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //获取请求参数，且该参数获取必须在proceed之前
        Map<String, Object> paramsMap = getRequestParam(invocation, request);
        //新建计时器
        StopWatch stopWatch = new StopWatch();
        //开始计时
        stopWatch.start();
        try{
            //调用升级的action方法
            Object result = invocation.proceed();
            //暂停计时
            stopWatch.stop();
            //耗时
            long spentTime = stopWatch.getTime();
            //打印INFO日志
            logInfo(invocation, request, paramsMap, result, spentTime);
            return result;
        } catch (Throwable e){
            //暂停计时
            if(stopWatch.isStarted() || stopWatch.isSuspended()){
                stopWatch.stop();
            }
            //耗时
            long spentTime = stopWatch.getTime();
            //打印ERROR日志
            logError(invocation, request, paramsMap, spentTime, e);
            throw new Throwable(e);
        }
    }
    /**
     * @Description 记录INFO日志
     * @Version  1.0
     */
    private void logInfo(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, Object result, long spentTime){
        String log = StringUtils.join(NEW_LINE, MSG_CONTROLLER, invocation.getThis().getClass(), ".", invocation.getMethod().getName(), NEW_LINE);
        log = StringUtils.join(log, MSG_ACCESS_URL, request.getRequestURL(), NEW_LINE);
        log = StringUtils.join(log, MSG_METHOD, request.getMethod(), NEW_LINE);
        log = StringUtils.join(log, MSG_PARAMS, paramsMap, NEW_LINE);
        log = StringUtils.join(log, MSG_TIME , spentTime, MILLI_SECOND, NEW_LINE);
        if(ObjectUtils.isEmpty(result)){
            log = StringUtils.join(log, MSG_RETURN_VALUE, result, NEW_LINE);
        } else if(result instanceof ResponseEntity){
            log = StringUtils.join(log, MSG_RETURN_VALUE, JSONUtils.toJSONString(((ResponseEntity)result).getBody()), NEW_LINE);
        } else {
            log = StringUtils.join(log, MSG_RETURN_VALUE, JSONUtils.toJSONString(result), NEW_LINE);
        }
        log = StringUtils.join(log, MSG_DATA_SIZE, ObjectSizeUtil.getObjectSizeUnit(result), NEW_LINE);
        LoggerUtil.info(invocation.getThis().getClass(), log);
    }
    /**
     * @Description 异常日志
     * @Version  1.0
     */
    private void logError(MethodInvocation invocation, HttpServletRequest request, Map<String, Object> paramsMap, long spentTime, Throwable e){
        String log = StringUtils.join(NEW_LINE, MSG_CONTROLLER, invocation.getThis().getClass(), ".", invocation.getMethod().getName(), NEW_LINE);
        log = StringUtils.join(log, MSG_ACCESS_URL, request.getRequestURL(), NEW_LINE);
        log = StringUtils.join(log, MSG_METHOD, request.getMethod(), NEW_LINE);
        log = StringUtils.join(log, MSG_PARAMS, paramsMap, NEW_LINE);
        log = StringUtils.join(log, MSG_TIME , spentTime, MILLI_SECOND, NEW_LINE);
        log = StringUtils.join(log, MSG_EXCEPTION , e.getStackTrace()[0], " ", e, NEW_LINE);
        LoggerUtil.error(invocation.getThis().getClass(), log);

    }
    /**
     * @Description 获取请求参数
     * @Version  1.0
     */
    private Map<String, Object> getRequestParam(MethodInvocation invocation, HttpServletRequest request){
        Map<String, Object> paramMap = new LinkedHashMap<>();
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        Parameter[] parameters = method.getParameters();
        if(ArrayUtils.isEmpty(parameters)){
            return null;
        }
        for(int i=0; i<parameters.length; i++){
            if(args[i] instanceof HttpServletRequest){
                Enumeration<String> params = request.getParameterNames();
                while (params.hasMoreElements()){
                    String key = params.nextElement();
                    paramMap.put(key, request.getParameter(key));
                }
            } else if(!(args[i] instanceof HttpServletResponse)){
                if(args[i] instanceof BaseRequest){
                    BaseRequest baseRequest = (BaseRequest) args[i];
                    //将用户信息设置如HttpServletRequest中
                    request.setAttribute(parameters[i].getName(), baseRequest);
                    paramMap.put(parameters[i].getName(), JSONUtils.toJSONString(baseRequest));
                } else {
                    paramMap.put(parameters[i].getName(), JSONUtils.toJSONString(args[i]));
                }
            }
        }
        return paramMap;
    }
}
