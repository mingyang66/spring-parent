### 死磕源码系列【spring之WebApplicationInitializer源码解析】

WebApplicationInitializer是在Servlet3.0+环境中实现的接口，以便以编程方式配置ServletContext，而不是传统的基于web.xml方式。

SpringServletContainerInitializer的SPI实现将由其自身检测，它本身由任何servlet3.0容器自动引导，有关这种引导机制的详细信息，请参考SpringServletContainerInitializer的Javadoc。

基于XML的传统方法，大多数构建web应用程序的Spring用户都需要注册Spring的DispatcherServlet；在WEB-INF/web.xml文件中通常是这样做的：

```xml
  <servlet>
    <servlet-name>dispatcher</servlet-name>
    <servlet-class>
      org.springframework.web.servlet.DispatcherServlet
    </servlet-class>
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>/WEB-INF/spring/dispatcher-config.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
  </servlet>
 
  <servlet-mapping>
    <servlet-name>dispatcher</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
```

基于WebApplicationInitializer接口注册DispatcherServlet的等效逻辑是：

```java
public class MyWebAppInitializer implements WebApplicationInitializer {
 
     @Override
     public void onStartup(ServletContext container) {
       XmlWebApplicationContext appContext = new XmlWebApplicationContext();
       appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
 
       ServletRegistration.Dynamic dispatcher =
         container.addServlet("dispatcher", new DispatcherServlet(appContext));
       dispatcher.setLoadOnStartup(1);
       dispatcher.addMapping("/");
     }
 
  }
```

作为上述方法的替代，你可以从org.springframework.web.servlet.support.AbstractDispatcherServletInitializer类继承实现。

如你所见，由于Servlet3.0+新的ServletContext#addServlet方法，我们实际上注册了DispatcherServlet实例对象，这意味着现在可以像对待任何其他对象一样对待，在本例中，通过构造函数注入的方式注入应用程序上下文。

这种风格即简单又简洁，不需要处理init params等，只是普通的JavaBean风格的属性和构造函数参数。在将Spring应用上下文中注入DispatcherServlet之前，你可以根据需要自由地创建和使用Spring应用程序上下文。

大多数主要的Spring web组件都已更新以支持这种注册方式，你将发现DispatcherServlet、FrameworkServlet、ContextLoaderListener、DelegatingFilterProxy现在都支持构造函数参数。即使某一个组件（如：非Spring、其它第三方）尚未专门更新以在WebApplicationInitializers中使用，在任何情况下，它们仍然可以使用，Servlet3.0 API允许以编程的方式设置init-params、context-params等。

##### 100%基于代码的配置方法

在上面的实例中WEB-INF/web.xml文件已成功替换为代码格式，但是实际的dispatcher-config.xml配置仍然基于XML。

WebApplicationInitializer非常适合与Spring的基于代码的JavaConfig @Configuration配置类一起使用。可以查看org.springframework.context.annotation.Configuration 的Javadoc详细文档信息。

下面的例子演示了如何使用Spring的org.springframework.web.context.support.AnnotationConfigWebApplicationContext代替XmlWebApplicationContext。

```java
  public class MyWebAppInitializer implements WebApplicationInitializer {
 
     @Override
     public void onStartup(ServletContext container) {
       // Create the 'root' Spring application context
       AnnotationConfigWebApplicationContext rootContext =
         new AnnotationConfigWebApplicationContext();
       rootContext.register(AppConfig.class);
 
       // Manage the lifecycle of the root application context
       container.addListener(new ContextLoaderListener(rootContext));
 
       // Create the dispatcher servlet's Spring application context
       AnnotationConfigWebApplicationContext dispatcherContext =
         new AnnotationConfigWebApplicationContext();
       dispatcherContext.register(DispatcherConfig.class);
 
       // Register and map the dispatcher servlet
       ServletRegistration.Dynamic dispatcher =
         container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
       dispatcher.setLoadOnStartup(1);
       dispatcher.addMapping("/");
     }
 
  }
```

作为上述方法的替代，你也可以通过实现org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer抽象类的方式。

请记住，WebApplicationInitializer实现是自动检测到的，因此，您可以自由地将它们打包到你认为合适的应用程序 中。

##### WebApplicationInitializer排序执行

WebApplicationInitializer实现可以选择在类级别用Spring的org.springframework.core.annotation.Order注解或者org.springframework.core.Ordered接口指定优先级顺序。

如果这样，初始化器将在调用之前排序。这为用户提供了一种机制来确保servlet容器初始化的顺序。这个特性的使用是很少见的，因为典型的应用程序很可能将所有容器初始化几种在一个WebApplicationInitializer中。

```java
public interface WebApplicationInitializer {

	/**
	 * 配置任何给定的servlets, filters, listeners
	 */
	void onStartup(ServletContext servletContext) throws ServletException;

}
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)