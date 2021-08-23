package com.emily.infrastructure.common.utils;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.constant.CharacterUtils;
import com.emily.infrastructure.common.utils.constant.CharsetUtils;
import com.emily.infrastructure.common.utils.io.IOUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpServletRequest请求类
 *
 * @author Emily
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
     * 是否存在servlet上下文
     *
     * @return
     */
    public static boolean isServletContext() {
        return RequestContextHolder.getRequestAttributes() == null ? false : true;
    }

    /**
     * 获取用户当前请求的HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            return attributes.getRequest();
        } catch (Exception ex) {
            throw new BusinessException(AppHttpStatus.IO_EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
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
            throw new BusinessException(AppHttpStatus.IO_EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
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

    /**
     * 开启请求时间记录
     */
    public static void startRequest() {
        if (!isServletContext()) {
            return;
        }
        //设置业务请求开始时间
        getRequest().setAttribute("startTime", System.currentTimeMillis());
    }

    /**
     * 获取请求开始到当前耗时
     *
     * @return
     */
    public static long getTime() {
        if (!isServletContext()) {
            return 0;
        }
        if (getRequest().getAttribute("startTime") == null) {
            return 0;
        }
        return System.currentTimeMillis() - Long.valueOf(getRequest().getAttribute("startTime").toString());
    }

    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }
}
