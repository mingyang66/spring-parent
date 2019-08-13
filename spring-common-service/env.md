#### Spring boot加载war、jar包之外的自定义配置文件之EnvironmentPostProcessor

通常spring boot项目的配置文件都是配置在classpath环境变量下面，系统会根据默认的路径去加载；但是如果项目打成war、jar包并且已经升级过了，这时候想改配置
文件这时候就需要重新打包了，这样很麻烦，而Spring boot也给我们提供了扩展的接口EnvironmentPostProcessor；

#### 1.EnvironmentPostProcessor源码
```
@FunctionalInterface
public interface EnvironmentPostProcessor {
    void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application);
}
```
>接口是一个函数式接口，只有一个方法postProcessEnvironment用来加载外部自定义配置

##### 2.看过源码后直接看实现方式
```
package com.yaomy.common.env;

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
```

#### 3.要想使自定义的EnvironmentPostProcessor生效必须配置一个配置文件
在resource目录下新增META-INF文件夹，在文件加下新建spring.facories文件，添加上如下配置
```
org.springframework.boot.env.EnvironmentPostProcessor=com.uufund.ecapi.config.properties.UserEnvironmentPostProcessor
```
#### 4.在application.properties配置文件中新增
```
spring.custom.file.loading=dev,devDb,devRedis,devSms
spring.custom.file.path=D:\\work\\workplace\\config\\
```



