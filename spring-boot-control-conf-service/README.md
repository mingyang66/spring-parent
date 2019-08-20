#### Spring boot加载自定义配置路径文件之EnvironmentPostProcessor

通常spring boot项目的配置文件都是配置在classpath环境变量下面，系统会默认使用ConfigFileApplicationListener去加载；但是如果项目打成war、jar包并且已经升级过了或者在项目之外有自定义的配置文件，这时候想改配置
文件这时候就需要重新打包了，这样很麻烦，而Spring boot也给我们提供了扩展的接口EnvironmentPostProcessor；

本文讲解自定义文件路径支持三种路径配置方式
* 相对路径，放在tomcat服务器同一级目录，优先级最高；
* 绝对路径，根据配置的路径加载配置文件，优先级第二；
* 默认方式，properties文件放在resources文件夹下，优先级排第三；

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
package com.yaomy.control.conf;


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
import java.util.Properties;

/**
 * @Description 自定义配置文件路径加载
 * @Date 2019/8/20 9:20
 * @Version  1.0
 */
public class ConfEnvironmentPostProcessor implements EnvironmentPostProcessor {
    /**
     * 默认的配置文件
     */
    private static final String DEAFULT_PROFILES = "classpath:application.properties";
    /**
     * 是否是相对位置，默认false
     */
    private static final String PROFILES_RELATIVE_POSITION = "spring.profiles.relative.position";
    /**
     * 配置文件的绝对路径
     */
    private static final String PROFILES_CONF_PATH = "spring.profiles.conf.path";
    /**
     * 配置文件的文件名，用逗号分开，只配置application-*.properties 星号代表的部分
     */
    private static final String PROFILES_INCLUDE_FILE = "spring.profiles.include";
    /**
     * 布尔TRUE字符串
     */
    private static final String BOOLEAN_TRUE = "true";
    /**
     * 自定义配置文件前缀
     */
    private static final String CONFIG_PATH = "config";
    /**
     * 文件前缀
     */
    private static final String FILE_PREFIX = "application";
    /**
     * 横线
     */
    private static final String FILE_LINE = "-";
    /**
     * 文件后缀
     */
    private static final String FILE_SUFFIX = ".properties";
    /**
     * 英文逗号
     */
    private static final String EN_COMMA = ",";
    /**
     * @Description 加载配置文件方法
     * @Version  1.0
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try{
            File file = ResourceUtils.getFile(DEAFULT_PROFILES);
            Properties properties = loadProperties(file);
            String rootPath = StringUtils.EMPTY;
            //相对位置 优先级第一
            if(StringUtils.equalsIgnoreCase(properties.getProperty(PROFILES_RELATIVE_POSITION), BOOLEAN_TRUE)){
                rootPath = getRootPath();
            }
            //绝对路径 优先级第二
            if(StringUtils.isBlank(rootPath) && StringUtils.isNotBlank(properties.getProperty(PROFILES_CONF_PATH))){
                rootPath = properties.getProperty(PROFILES_CONF_PATH);
            }
            if(StringUtils.isEmpty(rootPath)){
                return;
            }
            MutablePropertySources propertySources = environment.getPropertySources();
            File defaultFile = new File(StringUtils.join(rootPath, File.separator, CONFIG_PATH, File.separator, FILE_PREFIX, FILE_SUFFIX));
            if(defaultFile.exists()){
                Properties defaultProperties = loadProperties(defaultFile);
                propertySources.addFirst(new PropertiesPropertySource(StringUtils.join(FILE_PREFIX, FILE_SUFFIX), defaultProperties));
                System.out.println("加载配置文件："+StringUtils.join(FILE_PREFIX, FILE_SUFFIX));
                if(StringUtils.isNotBlank(defaultProperties.getProperty(PROFILES_INCLUDE_FILE))){
                    properties = defaultProperties;
                }
            }

            String[] list = StringUtils.split(properties.getProperty(PROFILES_INCLUDE_FILE), EN_COMMA);
            if(null == list || list.length == 0){
                return;
            }

            for(String fileName : list){
                File f = new File(StringUtils.join(rootPath, File.separator, CONFIG_PATH, File.separator, FILE_PREFIX, FILE_LINE, fileName, FILE_SUFFIX));
                if(f.exists()){
                    propertySources.addFirst(new PropertiesPropertySource(StringUtils.join(FILE_PREFIX, FILE_LINE, fileName, FILE_SUFFIX), loadProperties(f)));
                    System.out.println("加载配置文件："+StringUtils.join(FILE_PREFIX, FILE_LINE, fileName, FILE_SUFFIX));
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * @Description 加载配置文件
     * @Version  1.0
     */
    private Properties loadProperties(File f) throws IOException{
        FileSystemResource resource = new FileSystemResource(f);
        return PropertiesLoaderUtils.loadProperties(resource);
    }
    /**
     * @Description 获取当前项目的根路径
     * @Version  1.0
     */
    private String getRootPath() throws IOException{
        File rootFile = new File("");
        String rootPath = rootFile.getCanonicalPath();
        return StringUtils.substring(rootPath, 0, StringUtils.lastIndexOf(rootPath, File.separator));
    }
}

```

#### 3.要想使自定义的EnvironmentPostProcessor生效必须配置一个配置文件
在resource目录下新增META-INF文件夹，在文件加下新建spring.facories文件，添加上如下配置
```
org.springframework.boot.env.EnvironmentPostProcessor=com.yaomy.control.conf.ConfEnvironmentPostProcessor
```
#### 4.在application.properties配置文件中新增
```
###优先级第一，相对位置，默认false
spring.profiles.relative.position=true
###优先级第二，配置文件绝对路径，只有在spring.profiles.relative.position时才起作用
spring.profiles.conf.path=D:\\work\\workplace\\config\\test\\config
###配置文件名
spring.profiles.include=dev,devSms
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
>上面的分析我们知道了是通过监听器监听到事件之后触发自定义配置类的加载，那监听器又是在哪里启动及加载呢？它是在启动类SpringApplication中加载的，如下：
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
}    
```    
>TIPS：上面的从加载监听器->监听事件->加载自定义配置类->调用自定义配置类方法，整个流程都分析完毕了，那我会有一个疑问？事件是在哪里，何时发布？

#### 6.事件的广播发布

```
public class SpringApplication {

    	public ConfigurableApplicationContext run(String... args) {
    		StopWatch stopWatch = new StopWatch();
    		stopWatch.start();
    		ConfigurableApplicationContext context = null;
    		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    		configureHeadlessProperty();
    		//获取应用程序的SpringApplicationEvent事件监听器包括ConfigFileApplicationListener
    		SpringApplicationRunListeners listeners = getRunListeners(args);
    		//启动并开始事件广播
    		listeners.starting();

    	}

        //获取应用程序监听器SpringApplicationRunListener
    	private SpringApplicationRunListeners getRunListeners(String[] args) {
    		Class<?>[] types = new Class<?>[] { SpringApplication.class, String[].class };
    		return new SpringApplicationRunListeners(logger, getSpringFactoriesInstances(
    				SpringApplicationRunListener.class, types, this, args));
    	}
    	//启动事件广播
        public void starting() {
            for (SpringApplicationRunListener listener : this.listeners) {
                //调用EventPublishingRunListener中的starting方法
                listener.starting();
            }
        }
}
``` 
>EventPublishingRunListener监听器中有一个SimpleApplicationEventMulticaster广播类，通过广播类向其它监听器发送广播事件
```
public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {

	private final SpringApplication application;

	private final String[] args;

	private final SimpleApplicationEventMulticaster initialMulticaster;

	public EventPublishingRunListener(SpringApplication application, String[] args) {
		this.application = application;
		this.args = args;
		this.initialMulticaster = new SimpleApplicationEventMulticaster();
		for (ApplicationListener<?> listener : application.getListeners()) {
			this.initialMulticaster.addApplicationListener(listener);
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void starting() {
	    //广播事件
		this.initialMulticaster.multicastEvent(
				new ApplicationStartingEvent(this.application, this.args));
	}
}	
```	
通过上面一步一步的源码分析，从事件发布到监听到自定义配置文件加载的整个过程都串联了起来

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-conf-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-conf-service)

