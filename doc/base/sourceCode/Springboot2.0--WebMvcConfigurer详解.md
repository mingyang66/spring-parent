## Springboot2.0--WebMvcConfigurer详解

>
WebMvcConfigurer是spring内部配置的一种方式，使用JavaBean的方式代替传统的xml配置；也可以自定义扩展配置类，实现方式是继承WebMvcConfigurer接口；WebMvcConfigurer其实就是一个接口，具体的配置是由实现类来决定的，现在会有两个问题，具体的实现类有哪些？这些实现类是如何加载到容器之中并生效的？带着这两个问题开启我们源码的探索之旅。

## WebMvcConfigurer具体实现类

##### 1.系统自带实现类

- WebMvcAutoConfigurationAdapter是Spring的主要配置类（会集成其它配置类到当前类），几乎所有的缺省配置都是在此类中配置，此配置类的优先级是0

- SpringDataWebConfiguration一些系统配置类，暂无仔细研究，此配置的优先级是最高的
- WebMvcConfigurerComposite此类是一个委托代理类，在DelegatingWebMvcConfiguration类中实例化，并将系统自带或者自定义的配置类注入到成员变量delegates之中。

##### 2.自定义配置实现类

> 自定义实现类需要实现WebMvcConfigurer接口，优先级默认介于上述两个系统自带配置类，可以通过@Order注解或者Order接口来调整优先级

自定义配置文件示例如下(以配置路由规则方法为例)：

```java
@Configuration
@EnableConfigurationProperties(WebProperties.class)
public class WebAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private WebProperties webProperties;

    /**
     * 配置路由规则
     *
     * @param configurer
     */
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        //区分大小写,默认true
        matcher.setCaseSensitive(webProperties.getPath().isCaseSensitive());
        //是否去除前后空格,默认false
        matcher.setTrimTokens(webProperties.getPath().isTrimTokens());
        //分隔符
        matcher.setPathSeparator(CharacterUtils.PATH_SEPARATOR);
        //是否缓存匹配规则,默认null等于true
        matcher.setCachePatterns(webProperties.getPath().isCachePatterns());
        //设置路由匹配规则
        configurer.setPathMatcher(matcher);
        //设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
        configurer.setUseTrailingSlashMatch(webProperties.getPath().isUseTrailingSlashMatch());
        //给所有的接口统一添加前缀
        configurer.addPathPrefix(webProperties.getPath().getPrefix(), c -> {
            if (c.isAnnotationPresent(RestController.class) || c.isAnnotationPresent(Controller.class)) {
                return true;
            }
            return false;
        });
    }

}
```

## 系统自带配置实现类和自定义配置实现类如何生效？

DelegatingWebMvcConfiguration是对Spring MVC进行配置的一个代理类，它结合缺省配置和用户自定义配置最终确定使用的配置。

DelegatingWebMvcConfiguration继承自WebMvcConfigurationSupport，而WebMvcConfigurationSupport为Spring
MVC提供缺省配置，它提供的就是上面提到的缺省配置。

看如下源码，DelegatingWebMvcConfiguration代理类会创建一个WebMvcConfigurerComposite代理类，并将容器之中的缺省配置类和自定义配置类注入到代理类之中；

```java
@Configuration(proxyBeanMethods = false)
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
  //创建代理类实例对象
	private final WebMvcConfigurerComposite configurers = new WebMvcConfigurerComposite();

	//将容器之中实现了WebMvcConfigurer接口的缺省配置类和自定义配置类注入到参数configurers之中
	@Autowired(required = false)
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
      //将配置类添加到代理类属性List集合中
			this.configurers.addWebMvcConfigurers(configurers);
		}
	}
	...
	}
```

看下代理类WebMvcConfigurerComposite的源码：

```java
/**
* 代理类继承WebMvcConfigurer接口，但是它是在DelegatingWebMvcConfiguration类中通过
* new 实例化的对象，所以不会注入到代理类之中
**/
class WebMvcConfigurerComposite implements WebMvcConfigurer {

	private final List<WebMvcConfigurer> delegates = new ArrayList<>();

 /**
 * 将WebMvcConfigurer实现类的bean添加到代理类集合
 **/
	public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.delegates.addAll(configurers);
		}
	}

	/**
	* 将路由规则配置添加到每个实现类中
	**/
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		for (WebMvcConfigurer delegate : this.delegates) {
			delegate.configurePathMatch(configurer);
		}
	}
	...
	}
```

EnableWebMvcConfiguration配置类继承了DelegatingWebMvcConfiguration代理类，类似@EnableWebMvc注解的功能，代理类中初始化配置的方法几乎都是在这个类中通过super来调用启动的；如下是调用代理类DelegatingWebMvcConfiguration获取控制器方法进行初始化的方法：

```java
		@Bean
		@Primary
		@Override
		public RequestMappingHandlerMapping requestMappingHandlerMapping(
				@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
				@Qualifier("mvcConversionService") FormattingConversionService conversionService,
				@Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
			// Must be @Primary for MvcUriComponentsBuilder to work
			return super.requestMappingHandlerMapping(contentNegotiationManager, conversionService,
					resourceUrlProvider);
		}
```

父类DelegatingWebMvcConfiguration（WebMvcConfigurationSupport）的requestMappingHandlerMapping方法：

```java
	/**
	 * Return a {@link RequestMappingHandlerMapping} ordered at 0 for mapping
	 * requests to annotated controllers.
	 */
	@Bean
	@SuppressWarnings("deprecation")
	public RequestMappingHandlerMapping requestMappingHandlerMapping(
			@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager,
			@Qualifier("mvcConversionService") FormattingConversionService conversionService,
			@Qualifier("mvcResourceUrlProvider") ResourceUrlProvider resourceUrlProvider) {
		//初始化RequestMappingHandlerMapping类，初始化完成后会调用afterPropertiesSet方法
    //接下来就是加载容器中所有的控制器方法信息，将RequestMappingInfo和HandlerMethod方法注册入
    //缓存
		RequestMappingHandlerMapping mapping = createRequestMappingHandlerMapping();
		mapping.setOrder(0);
		mapping.setInterceptors(getInterceptors(conversionService, resourceUrlProvider));
		mapping.setContentNegotiationManager(contentNegotiationManager);
		mapping.setCorsConfigurations(getCorsConfigurations());

		PathMatchConfigurer configurer = getPathMatchConfigurer();

		Boolean useSuffixPatternMatch = configurer.isUseSuffixPatternMatch();
		if (useSuffixPatternMatch != null) {
			mapping.setUseSuffixPatternMatch(useSuffixPatternMatch);
		}
		Boolean useRegisteredSuffixPatternMatch = configurer.isUseRegisteredSuffixPatternMatch();
		if (useRegisteredSuffixPatternMatch != null) {
			mapping.setUseRegisteredSuffixPatternMatch(useRegisteredSuffixPatternMatch);
		}
		Boolean useTrailingSlashMatch = configurer.isUseTrailingSlashMatch();
		if (useTrailingSlashMatch != null) {
			mapping.setUseTrailingSlashMatch(useTrailingSlashMatch);
		}

		UrlPathHelper pathHelper = configurer.getUrlPathHelper();
		if (pathHelper != null) {
			mapping.setUrlPathHelper(pathHelper);
		}
		PathMatcher pathMatcher = configurer.getPathMatcher();
		if (pathMatcher != null) {
			mapping.setPathMatcher(pathMatcher);
		}
		Map<String, Predicate<Class<?>>> pathPrefixes = configurer.getPathPrefixes();
		if (pathPrefixes != null) {
			mapping.setPathPrefixes(pathPrefixes);
		}

		return mapping;
	}
```

>
具体的缺省配置基本上都是在代理类DelegatingWebMvcConfiguration的父类WebMvcConfigurationSupport中实现的，像RequestMappingHandlerMapping初始化、RequestMappingHandlerAdapter适配器类初始化等等；

WebMvcAutoConfigurationAdapter是一个适配器类，使用@Import注解将EnableWebMvcConfiguration配置引入，可以说是spring
缺省配置的一个集合；

WebMvcAutoConfiguration是一个自动化配置类，会在bean
WebMvcConfigurationSupport不存在的时候初始化，所以这也是我们实现自定义配置的时候为什么不继承WebMvcConfigurationSupport类的原因；

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)