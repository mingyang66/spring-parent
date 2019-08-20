package com.yaomy.control.common.env;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * @Description: 用户自定义加载配置文件
 * @ProjectName: EM.FrontEnd.PrivateEquity.electronic-contract
 * @Package: com.uufund.ecapi.config.properties.UserEnvironmentPostProcessor
 * @Date: 2019/8/12 14:52
 * @Version: 1.0
 */
public class UserEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try{
            File file = ResourceUtils.getFile("classpath:application.properties");
            Properties properties = loadProperties(file);
            MutablePropertySources propertySources = environment.getPropertySources();
            if(properties.containsKey("spring.custom.file.loading") && StringUtils.isNotBlank(properties.getProperty("spring.custom.file.loading"))){
                List<String> list = Lists.newArrayList(StringUtils.split(properties.getProperty("spring.custom.file.loading"), ","));
                for(String fileName : list){
                    File f = new File(StringUtils.join(properties.getProperty("spring.custom.file.path"), "application-", fileName, ".properties"));
                    if(f.exists()){
                        //添加具有最高优先级的给定属性源对象
                        propertySources.addFirst(new PropertiesPropertySource(StringUtils.join("application-", fileName, "properties"), loadProperties(f)));
                    }
                }
            }
        } catch (FileNotFoundException e){
           e.printStackTrace();
        }
    }
    /**
     * @Description 加载配置文件
     * @Date 2019/8/12 17:45
     * @Version  1.0
     */
    private Properties loadProperties(File f) {
        FileSystemResource resource = new FileSystemResource(f);
        try {
            return PropertiesLoaderUtils.loadProperties(resource);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to load local settings from " + f.getAbsolutePath(), ex);
        }
    }
}
