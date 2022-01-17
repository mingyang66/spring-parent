package com.emily.infrastructure.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description: 数据源配置文件
 * @author: Emily
 * @create: 2020/05/14
 */
@ConfigurationProperties(prefix = MybatisProperties.PREFIX)
public class MybatisProperties {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.mybatis";
    /**
     * 是否开启数据源组件, 默认：true
     */
    private boolean enabled = true;
    /**
     * 子类是否集成超类或接口上标记的注解，默认：true
     */
    private boolean checkClassInherited = true;
    /**
     * 子类中的方法是否继承父类或接口中继承方法的注解，默认：false
     */
    private boolean checkMethodInherited = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCheckClassInherited() {
        return checkClassInherited;
    }

    public void setCheckClassInherited(boolean checkClassInherited) {
        this.checkClassInherited = checkClassInherited;
    }

    public boolean isCheckMethodInherited() {
        return checkMethodInherited;
    }

    public void setCheckMethodInherited(boolean checkMethodInherited) {
        this.checkMethodInherited = checkMethodInherited;
    }
}
