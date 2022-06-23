### 死磕源码系列【springboot内嵌tomcat如何启用ServletContextInitializer的onStartup方法进行初始化Servlet\Filiter\EventListener】

参考:[死磕源码系列springboot之ServletContextInitializerBeans如何将Filter、Servlet、EventListener注册到ServletContext中源码解](https://mingyang.blog.csdn.net/article/details/111059405)

在springboot的jar包中有一个TomcatStarter类，是ServletContainerInitializer的子类，会对ServletContextInitializer的相关实现类及包装类进行初始化；

##### TomcatStarter源码如下：

```java
class TomcatStarter implements ServletContainerInitializer {

	private static final Log logger = LogFactory.getLog(TomcatStarter.class);
	//ServletContextInitializer相关实现类及包装类
	private final ServletContextInitializer[] initializers;

	private volatile Exception startUpException;
	//创建一个TomcatStarter实例对象
	TomcatStarter(ServletContextInitializer[] initializers) {
		this.initializers = initializers;
	}

	@Override
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext) throws ServletException {
		try {
      //通过循环的方式调用ServletContextInitializer的onStartup方法将Servlet\Filter\EventListener等注册到ServletContext上下文之中
			for (ServletContextInitializer initializer : this.initializers) {
				initializer.onStartup(servletContext);
			}
		}
		catch (Exception ex) {
			this.startUpException = ex;
			// Prevent Tomcat from logging and re-throwing when we know we can
			// deal with it in the main thread, but log for information here.
			if (logger.isErrorEnabled()) {
				logger.error("Error starting Tomcat context. Exception: " + ex.getClass().getName() + ". Message: "
						+ ex.getMessage());
			}
		}
	}

	Exception getStartUpException() {
		return this.startUpException;
	}

}

```

##### TomcatStarter在TomcatServletWebServerFactory#configureContext方法中进行初始化，并且在org.apache.catalina.core.StandardContext#startInternal方法中启用

```java
	protected void configureContext(Context context, ServletContextInitializer[] initializers) {
		//实例化TomcatStarter对象
    TomcatStarter starter = new TomcatStarter(initializers);
		if (context instanceof TomcatEmbeddedContext) {
			TomcatEmbeddedContext embeddedContext = (TomcatEmbeddedContext) context;
			embeddedContext.setStarter(starter);
			embeddedContext.setFailCtxIfServletStartFails(true);
		}
    //将TomcatStarter对象初始化到context上下文的initializers变量
		context.addServletContainerInitializer(starter, NO_CLASSES);
  }
```

##### ServletContext对象是在StandardContext#getServletContext方法中进行初始化

```java
    @Override
    public ServletContext getServletContext() {
        if (context == null) {
          //ApplicationContext是ServletContext的子类
            context = new ApplicationContext(this);
            if (altDDName != null)
                context.setAttribute(Globals.ALT_DD_ATTR,altDDName);
        }
        return context.getFacade();
    }
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

