### RequestMappingHandlerMapping源码解析

> RequestMappingHandlerMapping的作用是在容器启动后将系统中所有控制器方法的请求条件（RequestMappingInfo）和控制器方法(
> HandlerMethod)的对应关系注册到RequestMappingHandlerMapping Bean的内存中，待接口请求系统的时候根据请求条件和内存中存储的系统接口信息比对，再执行对应的控制器方法。

##### 1.首先分析下HandlerMethod（控制器方法包装对象）

```java
public class HandlerMethod {
	//bean名称，调试的时候看到是字符串控制器名称（首字母小写）
	private final Object bean;
  //bean工厂类，个人调试传入的是DefaultListableBeanFactory
	@Nullable
	private final BeanFactory beanFactory;
	//方法所属类
	private final Class<?> beanType;
	//控制器方法
	private final Method method;
	//桥接方法，如果method是原生的，这个属性就是method
	private final Method bridgedMethod;
	//封装方法参数实例
	private final MethodParameter[] parameters;
	//Http状态码
	@Nullable
	private HttpStatus responseStatus;
	//ResponseStatus注解的reason值
	@Nullable
	private String responseStatusReason;
	//使用createWithResolvedBean方法创建的HttpMethod方法对象
	@Nullable
	private HandlerMethod resolvedFromHandlerMethod;
	//getInterfaceParameterAnnotations获取
	@Nullable
	private volatile List<Annotation[][]> interfaceParameterAnnotations;
	//类描述，使用initDescription方法解析beanType和method获得
	private final String description;
}
```

> HandlerMethod类用于封装控制器方法信息，包含类信息、方法Method对象、参数、注解等信息，具体的接口请求是可以根据封装的信息调用具体的方法来执行业务逻辑；

##### 2.RequestMappingInfo(请求信息)

> RequestMappingInfo其实就是将我们熟悉的@RequestMapping注解的信息数据封装到了RequestMappingInfo
> POJO对象之中，然后和HandlerMethod做映射关系存入缓存之中；

首先看下@RequestMapping注解，这个注解会将请求映射到控制器方法之上；

```
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMapping {

	/**
	 * 请求映射别名
	 */
	String name() default "";

	/**
	 * url映射路径，等价于value属性
	 * 数组类型，同一个控制器支持多个路由请求
	 * 支持ant风格和通配符表达式
	 */
	@AliasFor("path")
	String[] value() default {};

	/**
	 * url映射路径，等价于path属性
	 * 数组类型，同一个控制器支持多个路由请求
	 * 支持ant风格和通配符表达式
	 */
	@AliasFor("value")
	String[] path() default {};

	/**
	 * Http请求方法，支持GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE请求
	 * 默认请求方法是GET请求,同时可以支持多个请求类型
	 */
	RequestMethod[] method() default {};

	/**
	 * 此属性可以指定同一个URL路由由多个控制器来处理，而参数的值不同，每个控制器可以根据
	 * 不同的参数值来处理请求
	 */
	String[] params() default {};

	/**
	 * 请求头中必须包含指定的参数才可以处理请求
	 */
	String[] headers() default {};

	/**
	 * 匹配请求Content-Type的媒体类型，示例如下：
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * consumes = MediaType.TEXT_PLAIN_VALUE
	 * 也可以使用!来表示非
	 */
	String[] consumes() default {};

	/**
	 * 定义控制器处理程序生成的数据媒体类型，示例如下：
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = MediaType.TEXT_PLAIN_VALUE
	 * produces = "text/plain;charset=UTF-8"
	 * 也可以使用!来表示非
	 */
	String[] produces() default {};

}

```

下面看下RequestMappingHandlerMapping类是如何获取注解@RequestMapping的数据并封装到RequestMappingInfo对象的方法中去：

```java
    @Nullable
    private RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
       //获取@RequestMapping注解类对象
        RequestMapping requestMapping = (RequestMapping)AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
        RequestCondition<?> condition = element instanceof Class ? this.getCustomTypeCondition((Class)element) : this.getCustomMethodCondition((Method)element);
        //调用创建RequestMappingInfo对象的方法
        return requestMapping != null ? this.createRequestMappingInfo(requestMapping, condition) : null;
    }
```

createRequestMappingInfo方法使用RequestMappingInfo对象将RequestMapping对象中的相关信息 组装起来，并返回一个RequestMappingInfo对象；

```java
   protected RequestMappingInfo createRequestMappingInfo(RequestMapping requestMapping, @Nullable RequestCondition<?> customCondition) {
        Builder builder = RequestMappingInfo.paths(
          //解析路径信息
          this.resolveEmbeddedValuesInPatterns(requestMapping.path()))
          //请求方法，如GET、POST
          .methods(requestMapping.method())
          //请求参数数据
          .params(requestMapping.params())
          //请求头数据
          .headers(requestMapping.headers())
          //控制器可以接收的数据媒体类型
          .consumes(requestMapping.consumes())
          //控制器处理完成后响应的数据类型
          .produces(requestMapping.produces())
          //参数名称
          .mappingName(requestMapping.name());
        if (customCondition != null) {
            builder.customCondition(customCondition);
        }

        return builder.options(this.config).build();
    }
```

##### 3.源码执行逻辑分析

先总结下RequestMappingHandlerMapping的工作流程大致如下：

- 容器将RequestMappingHandlerMapping组件注册入容器的时候，监测到了InitializingBean接口，注册完成后会执行afterPropertiesSet方法；
-

afterPropertiesSet方法会调用父类AbstractHandlerMethodMapping的afterPropertiesSet方法，然后调用initHandlerMethods方法，此方法会首先获取容器中所有bean的beanName,然后循环调用processCandidateBean方法；

- processCandidateBean方法首先会获取bean的Class类型，然后调用isHandler方法判定是否是控制器类，如果是则调用detectHandlerMethods方法；
-

detectHandlerMethods方法首先会通过MethodIntrospector.selectMethods方法获取bean中所有控制器方法的Method和RequestMappingInfo对应关系，然后循环调用registerHandlerMethod注册方法；
-
在registerHandlerMethod方法中会将对应关系分别注册入多个不同的Map关系映射中，其中mappingLookup集合Map是我们外部获取系统中所有URL的入口地址，urlLookup是前端发送request请求时根据url获取RequestMappingInfo信息的入口集合；

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200529150222763.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70)

上面的类图显示RequestMappingHandlerMapping实现了InitializingBean接口，容器会在初始化RequestMappingHandlerMapping完成后调用afterPropertiesSet方法；

```java
    public void afterPropertiesSet() {
        ...
        super.afterPropertiesSet();
    }
```

afterPropertiesSet方法会调用父类AbstractHandlerMethodMapping的方法：

```java
    public void afterPropertiesSet() {
        this.initHandlerMethods();
    }

    protected void initHandlerMethods() {
    		//获取容器中所有的beanName
        String[] var1 = this.getCandidateBeanNames();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String beanName = var1[var3];
            if (!beanName.startsWith("scopedTarget.")) {
                this.processCandidateBean(beanName);
            }
        }

        this.handlerMethodsInitialized(this.getHandlerMethods());
    }
		//扫描ApplicationContext容器中的Bean,判定并注册为HandlerMethods
    protected String[] getCandidateBeanNames() {
        return this.detectHandlerMethodsInAncestorContexts ? BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this.obtainApplicationContext(), Object.class) : this.obtainApplicationContext().getBeanNamesForType(Object.class);
    }
```

```java
    //判定当前类是否是控制器类
    protected boolean isHandler(Class<?> beanType) {
        return AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) || AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class);
    }
```

```java
  /**
  * 根据beanName获取类类型，根据类类型判定是否是控制器
  **/
  protected void processCandidateBean(String beanName) {
        Class beanType = null;

        try {
            beanType = this.obtainApplicationContext().getType(beanName);
        } catch (Throwable var4) {
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Could not resolve type for bean '" + beanName + "'", var4);
            }
        }
				//判定bean的类型是控制器类
        if (beanType != null && this.isHandler(beanType)) {
            this.detectHandlerMethods(beanName);
        }

    }
		//获取控制器类中方法对象和RequestMappingInfo的对应关系
    protected void detectHandlerMethods(Object handler) {
        Class<?> handlerType = handler instanceof String ? this.obtainApplicationContext().getType((String)handler) : handler.getClass();
        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            //获取Method和RequestMappingInfo的对应关系
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType, (method) -> {
                try {
                    //获取控制器handler的Method和RequestMappingInfo对应关系
                    return this.getMappingForMethod(method, userType);
                } catch (Throwable var4) {
                    throw new IllegalStateException("Invalid mapping on handler class [" + userType.getName() + "]: " + method, var4);
                }
            });
            if (this.logger.isTraceEnabled()) {
                this.logger.trace(this.formatMappings(userType, methods));
            }
						//将控制器方法对应关系注册进入内存对象之中
            methods.forEach((method, mapping) -> {
                Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                this.registerHandlerMethod(handler, invocableMethod, mapping);
            });
        }

    }
```

将@RequestMapping注解信息封装成RequestMappingInfo信息

```java
    @Nullable
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
      //针对控制器方法也就是使用了@RequestMapping注解信息，创建一个RequestMappingInfo对象
      //如果是一个控制器的普通方法，则返回的info对象是null
      RequestMappingInfo info = this.createRequestMappingInfo(method);
        if (info != null) {
           //针对控制器类上注解信息，创建一个RequestMappingInfo信息
            RequestMappingInfo typeInfo = this.createRequestMappingInfo(handlerType);
            if (typeInfo != null) {
                //合并控制器类和控制器方法上的RequestMappingInfo信息
                info = typeInfo.combine(info);
            }
						//获取自定义的控制器路由前缀信息
            String prefix = this.getPathPrefix(handlerType);
            if (prefix != null) {
              //如果定义了路径前缀信息，考虑合并前缀信息
                info = RequestMappingInfo.paths(new String[]{prefix}).options(this.config).build().combine(info);
            }
        }

        return info;
    }
```

对应关系要注入的Map对象

```java
 private final Map<T, AbstractHandlerMethodMapping.MappingRegistration<T>> registry = new HashMap();
 //容器可以通过此对象获取系统之中的所有URL
 private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap();
 //存储URL和HandlerMethod的对应关系
 private final MultiValueMap<String, T> urlLookup = new LinkedMultiValueMap();
 private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap();
 private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap();
 private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
```

执行注册对应关系的最终方法：

```java
public void register(T mapping, Object handler, Method method) {
            if (KotlinDetector.isKotlinType(method.getDeclaringClass())) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 0 && "kotlin.coroutines.Continuation".equals(parameterTypes[parameterTypes.length - 1].getName())) {
                    throw new IllegalStateException("Unsupported suspending handler method detected: " + method);
                }
            }

            this.readWriteLock.writeLock().lock();

            try {
              //获取控制器方法对应的HandlerMethod方法
                HandlerMethod handlerMethod = AbstractHandlerMethodMapping.this.createHandlerMethod(handler, method);
                this.validateMethodMapping(handlerMethod, mapping);
                //将RequestMappingInfo和HandlerMethod对应关系存入Map集合
                this.mappingLookup.put(mapping, handlerMethod);
                List<String> directUrls = this.getDirectUrls(mapping);
                Iterator var6 = directUrls.iterator();

                while(var6.hasNext()) {
                    String url = (String)var6.next();
                  //将url和RequestMappingInfo信息存入集合
                    this.urlLookup.add(url, mapping);
                }

                String name = null;
                if (AbstractHandlerMethodMapping.this.getNamingStrategy() != null) {
                    name = AbstractHandlerMethodMapping.this.getNamingStrategy().getName(handlerMethod, mapping);
                    this.addMappingName(name, handlerMethod);
                }

                CorsConfiguration corsConfig = AbstractHandlerMethodMapping.this.initCorsConfiguration(handler, method, mapping);
                if (corsConfig != null) {
                    this.corsLookup.put(handlerMethod, corsConfig);
                }

                this.registry.put(mapping, new AbstractHandlerMethodMapping.MappingRegistration(mapping, handlerMethod, directUrls, name));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }

        }
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)


