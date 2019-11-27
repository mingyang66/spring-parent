package com.yaomy.control.common.control.utils;

import com.yaomy.control.common.control.utils.io.FileUtils;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 读取路由配置文件
 * @Author: 姚明洋
 * @Date: 2019/11/13 13:39
 * @Version: 1.0
 */
public class RouteUtils {
    /**
     * 路由配置文件地址
     */
    private static final String filePath = "classpath:route.url";
    public static List<String> list = Collections.emptyList();
    static {
        try {
            File file = ResourceUtils.getFile(filePath);
            list = FileUtils.readLines(file, CharsetUtils.UTF8);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            LoggerUtil.error(RouteUtils.class, "路由配置文件丢失"+e.toString());
        }
    }
    /**
     * 读取路由配置文件
     */
    public static List<String> readRoute(){
        return list;
    }

    /**
     * 新增路由
     */
    public static void addRoute(String...routes){
        if(ObjectUtils.isEmpty(routes) || routes.length == 0){
            return;
        }
        for(int i=0; i<routes.length; i++){
            if(!list.contains(routes[i])){
                list.add(routes[i]);
            }
        }
    }

    /**
     * 删除路由
     */
    public static void removeRoute(String...routes){
        if(ObjectUtils.isEmpty(routes) || routes.length == 0){
            return;
        }
        list.removeAll(Arrays.asList(routes));
    }
}
