#### Spring boot加载war、jar包(项目)之外的自定义配置文件之EnvironmentPostProcessor

通常spring boot项目的配置文件都是配置在classpath环境变量下面，系统会默认使用ConfigFileApplicationListener去加载；但是如果项目打成war、jar包并且已经升级过了或者在项目之外有自定义的配置文件，这时候想改配置
文件这时候就需要重新打包了，这样很麻烦，而Spring boot也给我们提供了扩展的接口EnvironmentPostProcessor；

#### 1.EnvironmentPostProcessor源码
```
@FunctionalInterface
public interface EnvironmentPostProcessor {
    void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application);
}
```
>接口是一个函数式接口，只有一个方法postProcessEnvironment用来加载外部自定义配置

##### 2.看过源码后直接看自定义实现方式
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
>上面是我们自定义配置文件的实现方式，接下来我们看下自定义配置文件时如何被加载的

#### 5.ConfigFileApplicationListener调用自定义配置文件后处理器
```
    private void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
        //获取系统中所有的后置处理器实现类
        List<EnvironmentPostProcessor> postProcessors = this.loadPostProcessors();
        //将当前的ConfigFileApplicationListener类加入到列表中
        postProcessors.add(this);
        //按照优先级进行排序，自定义的优先级最低（addFirst），排在最后，所以最后加入的属性会覆盖之前的，所以自定义的优先级最高，不过使用addLast添加则优先级相反
        AnnotationAwareOrderComparator.sort(postProcessors);
        Iterator var3 = postProcessors.iterator();

        while(var3.hasNext()) {
            EnvironmentPostProcessor postProcessor = (EnvironmentPostProcessor)var3.next();
            //调用EnvironmentPostProcessor的postProcessEnvironment方法
            postProcessor.postProcessEnvironment(event.getEnvironment(), event.getSpringApplication());
        }

    }
    //获取系统中所有的EnvironmentPostProcessor实现类其中包括自定义实现及ConfigFileApplicationListener
    List<EnvironmentPostProcessor> loadPostProcessors() {
        return SpringFactoriesLoader.loadFactories(EnvironmentPostProcessor.class, this.getClass().getClassLoader());
    }
```
>onApplicationEvent调用上面的方法，而onApplicationEvent方法又是ConfigFileApplicationListener类实现SmartApplicationListener监听器的实现类，
会在事件发布后自动的加载配置文件
```
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            this.onApplicationEnvironmentPreparedEvent((ApplicationEnvironmentPreparedEvent)event);
        }

        if (event instanceof ApplicationPreparedEvent) {
            this.onApplicationPreparedEvent(event);
        }

    }
```    
>上面的分析我们知道了是通过监听器监听到事件之后出发自定义配置类的加载，那监听器又是在哪里启动及加载呢？它是在启动类SpringApplication中加载的，如下：
```
public class SpringApplication {
    ...
    private List<ApplicationListener<?>> listeners;
    ...
    public SpringApplication(ResourceLoader resourceLoader, Class... primarySources) {
        ...
        this.setListeners(this.getSpringFactoriesInstances(ApplicationListener.class));
        ...
    }
    private <T> Collection<T> getSpringFactoriesInstances(Class<T> type) {
        return this.getSpringFactoriesInstances(type, new Class[0]);
    }
```    
>TIPS：上面的从加载监听器->监听事件->加载自定义配置类->调用自定义配置类方法，整个流程都分析完毕了，那我会有一个疑问？事件是在哪里，何时发布？

#### 6.



