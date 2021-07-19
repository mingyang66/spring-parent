package com.emily.infrastructure.common.utils;

import com.emily.infrastructure.common.base.BaseRequest;
import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.SystemException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.constant.CharsetUtils;
import com.emily.infrastructure.common.utils.io.IOUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.google.common.collect.Maps;
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
import java.util.*;
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
    /**
     * 本地IP
     */
    private static final String LOCAL_IP = "127.0.0.1";
    /**
     * 服务器IP
     */
    private static String SERVER_IP = null;
    /**
     * 是否是内网正则表达式
     */
    private static String INTERNET = "^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$";

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
        if (StringUtils.equalsIgnoreCase("0:0:0:0:0:0:0:1", ip)) {
            ip = LOCAL_IP;
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
        Pattern reg = Pattern.compile(INTERNET);
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
    @Deprecated
    public static Map<String, Object> getParameterMap(MethodInvocation invocation) {
        Object[] args = invocation.getArguments();
        Parameter[] parameters = invocation.getMethod().getParameters();
        if (ArrayUtils.isEmpty(parameters)) {
            return Collections.emptyMap();
        }
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
            } else if (args[i] instanceof BaseRequest) {
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
        return JSONUtils.toJavaBean(JSONUtils.toJSONString(paramMap), Map.class);
    }

    /**
     * 获取参数对象
     *
     * @param params
     * @return
     */
    public static Map<String, Object> getParameterMap(byte[] params) {
        try {
            return JSONUtils.toObject(params, Map.class);
        } catch (Exception e) {
            return convertParameterToMap(IOUtils.toString(params, CharsetUtils.UTF_8));
        }
    }

    /**
     * 获取返回结果对象
     *
     * @param body 返回结果字节数组
     * @return
     */
    public static Object getResponseBody(byte[] body) {
        try {
            return JSONUtils.toObject(body, Object.class);
        } catch (Exception e) {
            return IOUtils.toString(body, CharsetUtils.UTF_8);
        }
    }

    /**
     * 将参数转换为Map类型
     *
     * @param param
     * @return
     */
    public static Map<String, Object> convertParameterToMap(String param) {
        if (StringUtils.isEmpty(param)) {
            return Collections.emptyMap();
        }
        Map<String, Object> pMap = Maps.newLinkedHashMap();
        String[] pArray = StringUtils.split(param, CharacterUtils.AND_AIGN);
        for (int i = 0; i < pArray.length; i++) {
            String[] array = StringUtils.split(pArray[i], CharacterUtils.EQUAL_SIGN);
            if (array.length == 2) {
                pMap.put(array[0], array[1]);
            }
        }
        return pMap;
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
        try {
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            return attributes.getRequest();
        } catch (Exception ex) {
            throw new SystemException(AppHttpStatus.IO_EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        }
    }

    /**
     * 获取当前请求的HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        try {
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            return attributes.getResponse();
        } catch (Exception ex) {
            throw new SystemException(AppHttpStatus.IO_EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        }
    }

    /**
     * 获取事物ID
     */
    public static String getTraceId() {
        try {
            Object tId = getRequest().getAttribute("T_ID");
            if (Objects.isNull(tId)) {
                tId = UUID.randomUUID().toString();
                getRequest().setAttribute("T_ID", tId);
                return String.valueOf(tId);
            }
            return String.valueOf(tId);
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }
}
