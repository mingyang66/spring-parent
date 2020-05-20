package com.sgrain.boot.common.utils;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import com.sgrain.boot.common.po.BaseRequest;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpServletRequest请求类
 */
public class RequestUtils {
    /**
     * unknown
     */
    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IP = "127.0.0.1";

    /**
     * 获取客户单IP地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 判断请求IP是否是内网IP
     */
    public static boolean isInnerIp(String ip) {
        String reg = "((192\\.168|172\\.([1][6-9]|[2]\\d|3[01]))"
                + "(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){2}|"
                + "^(\\D)*10(\\.([2][0-4]\\d|[2][5][0-5]|[01]?\\d?\\d)){3})";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(ip);
        return matcher.find();
    }

    /**
     * 获取服务器端的IP
     */
    public static String getServerIp() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                    if (ip != null
                            && !ip.isLoopbackAddress()
                            && !ip.getHostAddress().contains(":")) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            throw new BusinessException(AppHttpStatus.DATA_NOT_FOUND_EXCEPTION.getStatus(), "获取服务器端IP地址异常，" + e);
        }
        return LOCAL_IP;
    }

    /**
     * 获取请求参数
     *
     * @param invocation 方法拦截器连接点
     * @return 参数
     */
    public static Map<String, Object> getRequestParamMap(MethodInvocation invocation) {
        Parameter[] parameters = invocation.getMethod().getParameters();
        if (ArrayUtils.isEmpty(parameters)) {
            return Collections.emptyMap();
        }
        Object[] args = invocation.getArguments();
        Map<String, Object> paramMap = new LinkedHashMap<>();
        HttpServletRequest request = getRequest();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (args[i] instanceof HttpServletResponse) {
                continue;
            }
            if (args[i] instanceof HttpServletRequest) {
                Enumeration<String> params = request.getParameterNames();
                while (params.hasMoreElements()) {
                    String key = params.nextElement();
                    paramMap.put(key, request.getParameter(key));
                }
                continue;
            }
            if (args[i] instanceof BaseRequest) {
                BaseRequest baseRequest = (BaseRequest) args[i];
                paramMap.put(parameter.getName(), baseRequest);
            } else if (args[i] instanceof MultipartFile) {
                paramMap.put(parameter.getName(), ((MultipartFile) args[i]).getOriginalFilename());
            } else if (args[i] instanceof File) {
                paramMap.put(parameter.getName(), ((File) args[i]).getPath());
            } else {
                paramMap.put(parameter.getName(), args[i]);
            }
        }
        return paramMap;
    }

    /**
     * 获取参数中的系统信息
     * @param invocation 方法拦截器连接点
     * @return
     */
    public static BaseRequest.SystemInfo getRequestSystemInfo(MethodInvocation invocation) {
        Parameter[] parameters = invocation.getMethod().getParameters();
        BaseRequest.SystemInfo systemInfo = new BaseRequest.SystemInfo();
        if (ArrayUtils.isEmpty(parameters)) {
            return systemInfo;
        }
        Object[] args = invocation.getArguments();
        for (int i = 0; i < parameters.length; i++) {
            if (args[i] instanceof BaseRequest) {
                BaseRequest baseRequest = (BaseRequest) args[i];
                systemInfo = baseRequest.getSystemInfo();
                break;
            }
        }
        return systemInfo;
    }
    /**
     * 获取用户当前请求的HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        return attributes.getRequest();
    }

    /**
     * 获取当前请求的HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        return attributes.getResponse();
    }
}
