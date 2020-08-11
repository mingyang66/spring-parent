package com.sgrain.boot.common.utils;

import com.sgrain.boot.common.po.BaseRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Parameter;
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
    //本地IP
    private static final String LOCAL_IP = "127.0.0.1";
    //服务器IP
    private static String SERVER_IP = null;

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
     * @param ip
     * @return
     */
    public static boolean noInternet(String ip) {
        return !isInternet(ip);
    }

    /**
     * 判定是否是内网地址
     *
     * @param ip
     * @return
     */
    public static boolean isInternet(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        if (StringUtils.equals("0:0:0:0:0:0:0:1", ip)) {
            return true;
        }
        Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
        Matcher match = reg.matcher(ip);
        return match.find();
    }

    /**
     * 获取服务器端的IP
     */
    public static String getServerIp() {
        if (StringUtils.isNotEmpty(SERVER_IP)) {
            return SERVER_IP;
        }
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                String name = netInterface.getName();
                if (!StringUtils.contains(name, "docker") && !StringUtils.contains(name, "lo")) {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress ip = addresses.nextElement();
                        //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                        if (ip != null
                                && !ip.isLoopbackAddress()
                                && !ip.getHostAddress().contains(":")) {
                            SERVER_IP = ip.getHostAddress();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            SERVER_IP = LOCAL_IP;
        }
        return SERVER_IP;
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
            } else if (args[i] instanceof Throwable) {
                //参数异常信息，忽略
            } else {
                paramMap.put(parameter.getName(), args[i]);
            }
        }
        return paramMap;
    }

    /**
     * 获取参数中的系统信息
     *
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

    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }
}
