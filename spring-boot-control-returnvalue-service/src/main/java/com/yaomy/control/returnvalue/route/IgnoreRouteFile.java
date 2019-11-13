package com.yaomy.control.returnvalue.route;

import com.google.common.collect.Lists;
import com.yaomy.control.common.control.utils.CharsetUtils;
import com.yaomy.control.common.control.utils.io.FileUtils;
import com.yaomy.control.logback.utils.LoggerUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 读取路由配置文件
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2019/11/13 13:39
 * @Version: 1.0
 */
public class RouteFile {
    /**
     * 路由配置文件地址
     */
    private static final String filePath = "classpath:route.url";
    public static List<String> list = Collections.emptyList();

    /**
     * 读取路由配置文件
     */
    public static List<String> readRoute(){
        try {
            File file = ResourceUtils.getFile(filePath);
            list = FileUtils.readLines(file, CharsetUtils.UTF8);
        } catch (FileNotFoundException e){
           e.printStackTrace();
            LoggerUtil.error(RouteFile.class, "路由配置文件丢失"+e.toString());
        }
        return list;
    }

    /**
     * 新增路由
     * @param routes
     */
    public static void addRoure(String...routes){
        if(routes.length == 0){
            return;
        }
        list.addAll(Lists.newArrayList(routes));
    }
}
