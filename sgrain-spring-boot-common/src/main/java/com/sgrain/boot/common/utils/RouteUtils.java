package com.sgrain.boot.common.utils;

import com.sgrain.boot.common.utils.io.FileUtils;
import com.sgrain.boot.common.utils.io.IOUtils;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 读取路由配置文件
 * @Date: 2019/11/13 13:39
 * @Version: 1.0
 */
public class RouteUtils {
    /**
     * 路由配置文件地址
     */
    private static final String filePath = "route.url";
    public static List<String> list;
    static {
        InputStream inputStream = RouteUtils.class.getClassLoader().getResourceAsStream(filePath);
        list = IOUtils.readLines(inputStream, CharsetUtils.UTF_8);
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
     * 新增路由,以文件的方式
     */
    public static void addRoute(File file){
        if(ObjectUtils.isEmpty(file) || !file.exists()){
            return;
        }
        list.addAll(FileUtils.readLines(file, CharsetUtils.UTF_8));
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
