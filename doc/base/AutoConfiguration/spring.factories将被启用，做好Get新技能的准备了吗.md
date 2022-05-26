### spring.factories将被弃用，做好Get新技能的准备了吗

> 作为springboot的忠实用户，最近springboot升级到了2.7.0版本，其中有一项是改变原来的自动化配置注册方式；如果你之前写过相关starter类或者研究过自动化配置的源码知道，配置自动化配置类需要在META-INF/spring.factories文件中配置配置类，而最新版本是配置META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports文件中配置，不过目前是兼容两种配置模式共存。

##### 新老版本比对

| 原配置方式                | 当前配置方式                                                 |
| :------------------------ | ------------------------------------------------------------ |
| META-INF/spring.factories | META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports |
| @Configuration            | @AutoConfiguration                                           |

如果你自己写过自动化配置starter，那就赶快将注册方式有spring.factories方式更改为META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports。文件中的每一行代表了一个自动化配置类的全限定类名，[查看自动化配置实例](https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-autoconfigure/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports)。

springboot2.7.0目前会向后兼容老版本配置模式spring.factories。

##### 最新注解@AutoConfiguration

新注解@AutoConfiguration是被用在META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports中的自动化配置类上用来替换@Configuration、@AutoConfigurationAfter、@AutoConfigurationBefore注解，其中@Configuration对应的proxyBeanMethods属性值一直为false。

##### 新版本如何做到新老注册方式同时兼容？

1. SpringFactoriesLoader用来加载spring.factories配置类
2. ImportCandidates用来加载META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports配置文件；

看下其中一种场景AutoConfigurationExcludeFilter类兼容两种方案的源码：

```java
	protected List<String> getAutoConfigurations() {
		if (this.autoConfigurations == null) {
			List<String> autoConfigurations = new ArrayList<>(
					SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, this.beanClassLoader));
			ImportCandidates.load(AutoConfiguration.class, this.beanClassLoader).forEach(autoConfigurations::add);
			this.autoConfigurations = autoConfigurations;
		}
		return this.autoConfigurations;
	}
```

springboot2.7.0最新文档[https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)