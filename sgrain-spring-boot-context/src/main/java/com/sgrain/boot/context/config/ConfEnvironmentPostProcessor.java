package com.sgrain.boot.context.config;


import org.apache.commons.lang3.ArrayUtils;
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
 * @Version  1.0
 */
@Deprecated
public class ConfEnvironmentPostProcessor implements EnvironmentPostProcessor {
    /**
     * CLASSPATH路径开头
     */
    public static final String CLASSPATH = "classpath:";
    /**
     * 默认支持的配置文件
     */
    private static final String DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/,file:./,file:./config/";
    /**
     * 配置文件的绝对路径
     */
    private static final String CONFIG_LOCATION_PROPERTY = "spring.config.location";
    /**
     * 当前系统运行的环境
     */
    public static final String SPINRG_PROFILES_ACTIVE = "spring.profiles.active";
    /**
     * 配置文件的文件名，用逗号分开，只配置application-*.properties 星号代表的部分
     */
    private static final String SPRING_PROFILES_INCLUDE = "spring.profiles.include";
    /**
     * 自定义配置文件前缀
     */
    private static final String CONFIG_PATH = "config";
    /**
     * 文件前缀
     */
    private static final String DEFAULT_NAMES = "application";
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
     * 斜杠
     */
    public static final String BACK_SLASH = "/";
    /**
     * 点斜杠
     */
    public static final String BACK_SLASH_SPOT = "./";
    /**
     * @Description 加载配置文件方法
     * @Version  1.0
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //获取优先级最高的配置文件
        File file = getPriorityHighestFile();
        //加载配置application.properties文件
        Properties properties = loadProperties(file);
        if(null == properties || properties.isEmpty()){
            return;
        }
        //相对位置 优先级第二
        load(environment, properties, getRootPath());
        //绝对路径 优先级最高
        if(StringUtils.isNotBlank(properties.getProperty(CONFIG_LOCATION_PROPERTY))){
            load(environment, properties, properties.getProperty(CONFIG_LOCATION_PROPERTY));
        }
    }
    /**
     * @Description 加载配置文件
     * @Version  1.0
     */
    private void load(ConfigurableEnvironment environment, Properties properties, String rootPath){
        if(StringUtils.isEmpty(rootPath)){
            return;
        }
        MutablePropertySources propertySources = environment.getPropertySources();
        File defaultFile = new File(StringUtils.join(rootPath, File.separator, CONFIG_PATH, File.separator, DEFAULT_NAMES, FILE_SUFFIX));
        if(defaultFile.exists()){
            Properties defaultProperties = loadProperties(defaultFile);
            propertySources.addFirst(new PropertiesPropertySource(StringUtils.join(DEFAULT_NAMES, FILE_SUFFIX), defaultProperties));
            if(StringUtils.isNotBlank(defaultProperties.getProperty(SPRING_PROFILES_INCLUDE))){
                properties = defaultProperties;
            }
        }

        String[] array = StringUtils.split(properties.getProperty(SPRING_PROFILES_INCLUDE), EN_COMMA);
        if(ArrayUtils.isEmpty(array)){
            return;
        }
        //当前环境变量配置
        String profilesActive = properties.getProperty(SPINRG_PROFILES_ACTIVE);
        if(StringUtils.isNotBlank(profilesActive) && !ArrayUtils.contains(array, properties.getProperty(SPINRG_PROFILES_ACTIVE))){
            array = ArrayUtils.add(array, properties.getProperty(SPINRG_PROFILES_ACTIVE));
        }
        for(String fileName : array){
            File f = new File(StringUtils.join(rootPath, File.separator, CONFIG_PATH, File.separator, DEFAULT_NAMES, FILE_LINE, fileName, FILE_SUFFIX));
            if(f.exists()){
                propertySources.addFirst(new PropertiesPropertySource(StringUtils.join(DEFAULT_NAMES, FILE_LINE, fileName, FILE_SUFFIX), loadProperties(f)));
            }
        }
    }
    /**
     * @Description 获取优先级最高的配置文件
     * @Version  1.0
     */
    private File getPriorityHighestFile(){
        File file = null;
        //默认配置文件位置
        String[] locations = StringUtils.split(DEFAULT_SEARCH_LOCATIONS, EN_COMMA);
        for(String location:locations){
            String url = null;
            if(StringUtils.equals(location, StringUtils.join(CLASSPATH, BACK_SLASH))){
                url = StringUtils.join(location.replace(BACK_SLASH, StringUtils.EMPTY), DEFAULT_NAMES, FILE_SUFFIX);
            } else if(StringUtils.contains(location, BACK_SLASH_SPOT)){
                url = StringUtils.join(location, DEFAULT_NAMES, FILE_SUFFIX);
            } else {
                url = StringUtils.join(StringUtils.removeFirst(location, BACK_SLASH), DEFAULT_NAMES, FILE_SUFFIX);
            }
            File defaultFile = getResourceFile(url);
            if(null != defaultFile){
                file = defaultFile;
            }
        }
        return file;
    }
    /**
     * @Description 加载文件并返回File对象，如果文件不存在就返回null
     * @Version  1.0
     */
    private File getResourceFile(String url){
        if(StringUtils.isBlank(url)){
            return null;
        }
        try {
            File file = ResourceUtils.getFile(url);
            FileSystemResource resource = new FileSystemResource(file);
            //判断file系统相对位置文件是否存在
            if(resource.exists()){
                return file;
            }
        } catch (FileNotFoundException e){
        }
        return null;
    }
    /**
     * @Description 加载配置文件，返回Properties对象，否则返回null
     * @Version  1.0
     */
    private Properties loadProperties(File file){
        if(null == file || !file.exists()){
            return null;
        }
        FileSystemResource resource = new FileSystemResource(file);
        try {
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            return properties;
        } catch (IOException e){
            return null;
        }
    }
    /**
     * @Description 获取当前项目的根路径,即项目到文件夹名路径
     * @Version  1.0
     */
    private String getRootPath() {
        try {
            File rootFile = new File("");
            String rootPath = rootFile.getCanonicalPath();
            return StringUtils.substring(rootPath, 0, StringUtils.lastIndexOf(rootPath, File.separator));
        } catch (IOException e){
            return null;
        }
    }
}
