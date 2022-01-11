### springboot配置文件

##### 1.spring.config.name指定配置文件名

默认的配置文件名是application，可以使用spring.config.name指定自定义文件名，如下示例：

```
java -jar myproject.jar --spring.config.name=myproject
```



##### 2.spring.profiles.active激活指定的配置文件application-{profile}.properties

- 系统默认加载application-[default].properties配置文件;
- 使用逗号分隔多个profile配置文件;

- 在application配置文件中可以指定待激活的配置文件，示例：

```
spring.profiles.active=dev,test
```

系统会按照顺序加载application-dev.properties、application-test.properties配置文件，后面的配置文件会覆盖前面同名属性配置；

- 指定的配置文件要存放在和application.properties相同的目录

- 官网有这样一段说明：

```
If you have specified any files in spring.config.location, profile-specific variants of those files are not considered. Use directories in spring.config.location if you want to also use profile-specific properties.
```

翻译：如果你使用spring.config.location指定特定文件，特定文件变量（即spring.profiles.active）不会起作用，spring.config.location可以使用目录的形式指定特定文件，这样就可以使用spring.profiles.active激活特定文件变量。

##### 3.spring.config.location指定配置文件地址

- 示例如下：

```
java -jar myproject.jar --spring.config.location=classpath:/default.properties,classpath:/override.properties,file://path/
```

- 多个配置使用逗号分隔
- 如果指定的是目录要用/结尾

系统默认加载配置文件的顺序是classpath:/,classpath:/config,file:./,file:./config，配置文件的优先级恰好相反：

1. file:./config
2. file:./
3. classpath:/config/
4. classpath:/

当使用spring.config.location指定配置文件时将会替换掉以上默认位置的配置；例如，如果spring.config.location的配置是classpath:/custom-config/,file:./custom-config/，那么优先级顺序是：

1. file:./custom-config/
2. classpath:/custom-config/

源码org.springframework.boot.context.config.ConfigFileApplicationListener：

```java
        private Set<String> getSearchLocations() {
            //查看命令行环境变量、OS环境变量、系统环境变量是否包含指定的外部配置
            if (this.environment.containsProperty("spring.config.location")) {
                //如果包含就直接获取外部指定配置，替换掉默认的配置
                return this.getSearchLocations("spring.config.location");
            } else {
                Set<String> locations = this.getSearchLocations("spring.config.additional-location");
                locations.addAll(this.asResolvedSet(ConfigFileApplicationListener.this.searchLocations, "classpath:/,classpath:/config/,file:./,file:./config/"));
                return locations;
            }
        }
```

获取指定路径下的配置：

```java
	private Set<String> getSearchLocations(String propertyName) {
			Set<String> locations = new LinkedHashSet<>();
			if (this.environment.containsProperty(propertyName)) {
				for (String path : asResolvedSet(this.environment.getProperty(propertyName), null)) {
					if (!path.contains("$")) {
						path = StringUtils.cleanPath(path);
						if (!ResourceUtils.isUrl(path)) {
							path = ResourceUtils.FILE_URL_PREFIX + path;
						}
					}
					locations.add(path);
				}
			}
			return locations;
		}
```



使用逗号分隔符分割配置路径，并反转配置顺序：

```java
		private Set<String> asResolvedSet(String value, String fallback) {
			List<String> list = Arrays.asList(StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(
					(value != null) ? this.environment.resolvePlaceholders(value) : fallback)));
			//默认配置文件地址顺序反转
      Collections.reverse(list);
			return new LinkedHashSet<>(list);
		}
```

> 用在OS环境变量、系统属性配置、命令行参数
>

##### 4.spring.config.additional-location指定配置文件

该属性是用来指定默认配置之外的位置的配置文件，系统会有限使用指定的外部配置；例如，如果指定的配置是classpath:/custom-config/,file:./custom-config/，那么优先级顺序将会是：

1. file:./custom-config/
2. classpath:custom-config/
3. file:./config/
4. file:./
5. classpath:/config/
6. classpath:/

实例如下：

```
java -jar demo-0.0.1-SNAPSHOT.jar --spring.config.additional-location=file:///Users/yaomingyang/Downloads/config/
```



> 该配置属性用在命令行参数、OS环境变量、系统属性配置

##### 5.spring.profiles.include指定包含哪些特定配置文件

spring.profiles.active用来指定激活指定的配置文件，而spring.profiles.include可以用来指定激活配置文件还包含哪些配置文件；

如：默认配置文件application.properties

```
server.port=8003
##test、prod、publish
spring.profiles.active=dev
```

被激活的配置文件是application-dev.properties:

```
##testDb、testRedis;prodDb、prodRedis;publishDb、publishRedis
spring.profiles.include=devDb,devRedis
```

可以用来指定不同环境之间的切换及不同种类配置的加载。

参考：[https://docs.spring.io/spring-boot/docs/2.1.14.BUILD-SNAPSHOT/reference/html/boot-features-external-config.html](https://docs.spring.io/spring-boot/docs/2.1.14.BUILD-SNAPSHOT/reference/html/boot-features-external-config.html)
GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/profiles](https://github.com/mingyang66/spring-parent/tree/master/doc/profiles)