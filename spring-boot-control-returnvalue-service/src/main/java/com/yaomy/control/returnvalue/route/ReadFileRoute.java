package com.yaomy.control.returnvalue.route;

import com.yaomy.control.common.control.utils.CharsetUtils;
import com.yaomy.control.common.control.utils.io.FileUtils;
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
public class ReadFileRoute {
    /**
     * 路由配置文件地址
     */
    public static final String filePath = "classpath:route.url";

    /**
     * 读取路由配置文件
     */
    public static List<String> read(){
        try {
            File file = ResourceUtils.getFile(filePath);
            return FileUtils.readLines(file, CharsetUtils.UTF8);
        } catch (FileNotFoundException e){
            return Collections.emptyList();
        }
    }
}
