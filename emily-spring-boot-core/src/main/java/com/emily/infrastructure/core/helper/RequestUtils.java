package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.core.constant.AttributeInfo;
import com.emily.infrastructure.core.exception.BasicException;
import com.emily.infrastructure.core.exception.HttpStatusType;
import com.emily.infrastructure.core.exception.PrintExceptionInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;
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
     * 获取客户端IP
     *
     * @return 客户端IP
     */
    public static String getClientIp() {
        if (isServlet()) {
            return getClientIp(getRequest());
        }
        return LOCAL_IP;
    }

    /**
     * 获取客户单IP地址
     *
     * @param request 请求对象
     * @return 客户端IP
     */
    public static String getClientIp(HttpServletRequest request) {
        try {
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
        } catch (Exception exception) {
            return "";
        }
    }

    /**
     * 判定是否是内网地址
     *
     * @param ip IP地址
     * @return 是否内网
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
     *
     * @return 服务器端IP
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
     * 是否存在servlet上下文
     *
     * @return 是否servlet上下文
     */
    public static boolean isServlet() {
        return RequestContextHolder.getRequestAttributes() == null ? false : true;
    }

    /**
     * 获取用户当前请求的HttpServletRequest
     *
     * @return 请求对象
     */
    public static HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            return attributes.getRequest();
        } catch (Exception ex) {
            throw new BasicException(HttpStatusType.EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        }
    }

    /**
     * 获取当前请求的HttpServletResponse
     *
     * @return 响应对象
     */
    public static HttpServletResponse getResponse() {
        try {
            ServletRequestAttributes attributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            return attributes.getResponse();
        } catch (Exception ex) {
            throw new BasicException(HttpStatusType.EXCEPTION.getStatus(), PrintExceptionInfo.printErrorInfo(ex));
        }
    }

    public static void setServerIp(String serverIp) {
        SERVER_IP = serverIp;
    }

    /**
     * 获取耗时字段
     *
     * @return 耗时
     */
    public static long getSpentTime() {
        if (!RequestUtils.isServlet()) {
            return 0L;
        }
        Object time = getRequest().getAttribute(AttributeInfo.SPENT_TIME);
        if (Objects.nonNull(time)) {
            return Long.valueOf(String.valueOf(time));
        }
        return 0L;
    }

    /**
     * 获取请求头，请求头必须传
     *
     * @param header 请求头
     * @return 请求头结果
     */
    public static String getHeader(String header) {
        return getHeader(header, true);
    }

    /**
     * 获取请求头
     *
     * @param header   请求头
     * @param required 是否允许请求头为空或者不传递，true-是；false-否
     * @return 请求头结果
     */
    public static String getHeader(String header, boolean required) {
        if (header == null || header.length() == 0) {
            return header;
        }
        String value = getRequest().getHeader(header);
        if (!required) {
            return value;
        }
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException(HttpStatusType.ILLEGAL_ARGUMENT.getMessage());
        }
        return value;
    }
}
