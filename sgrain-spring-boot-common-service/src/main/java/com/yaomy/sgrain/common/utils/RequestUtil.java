package com.yaomy.sgrain.common.utils;

import com.yaomy.sgrain.common.po.BaseRequest;
import com.yaomy.sgrain.common.utils.json.JSONUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: HttpServlet
 * @Date: 2019/5/21 13:16
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class RequestUtil {
    /**
     * unknown
     */
    private static final String UNKNOWN = "unknown";
    private static final String LOCAL_IP = "127.0.0.1";
    /**
     * @Description 获取客户单IP地址
     * @Date 2019/5/21 13:17
     * @Version  1.0
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
      * @Description 判断请求IP是否是内网IP
      * @Author 姚明洋
      * @Date 2019/5/27 13:44
      * @Version  1.0
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
     * @Description 获取服务器端的IP
     * @Author 姚明洋
     * @Date 2019/9/2 15:17
     * @Version  1.0
     */
    public static String getServerIp(){
        try{
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()){
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()){
                    InetAddress ip = addresses.nextElement();
                    //loopback地址即本机地址，IPv4的loopback范围是127.0.0.0 ~ 127.255.255.255
                    if (ip != null
                            && ip instanceof Inet4Address
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":")==-1){
                        System.out.println("本机的IP = " + ip.getHostAddress());
                        return ip.getHostAddress();
                    }
                }
            }
        }catch(Exception e){
        }
       return LOCAL_IP;
    }

    /**
     * @Description 获取请求参数
     * @Version  1.0
     */
    public static Map<String, Object> getRequestParam(HttpServletRequest request, MethodInvocation invocation){
        Object[] args = invocation.getArguments();
        Method method = invocation.getMethod();
        Parameter[] parameters = method.getParameters();
        if(ArrayUtils.isEmpty(parameters)){
            return Collections.emptyMap();
        }
        Map<String, Object> paramMap = new LinkedHashMap<>();
        for(int i=0; i<parameters.length; i++){
            if(args[i] instanceof HttpServletRequest){
                Enumeration<String> params = request.getParameterNames();
                while (params.hasMoreElements()){
                    String key = params.nextElement();
                    paramMap.put(key, request.getParameter(key));
                }
            } else if((args[i] instanceof BaseRequest)){
                BaseRequest baseRequest = (BaseRequest) args[i];
                paramMap.putAll(JSONUtils.toJavaBean(JSONUtils.toJSONString(baseRequest), Map.class));
            } else if(!(args[i] instanceof HttpServletResponse)){
                paramMap.put(parameters[i].getName(), JSONUtils.toJSONString(args[i]));
            }
        }
        return paramMap;
    }
}
