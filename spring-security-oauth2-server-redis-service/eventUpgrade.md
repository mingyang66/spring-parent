### Spring Security用户认证成功失败源码分析
[Spring boot Security OAuth2用户登录失败事件发布及监听](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/event.md)<br>
[Spring Security用户认证成功失败自定义实现](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgradeCode.md)

 通常用户登录成功或者失败之后要做一些处理，比如日志记录、数据初始化等等；Spring中提供了事件及监听器，而Spring Security很好的运用了这一特点，
 框架中用了很多的事件来处理,要想很好的控制这些我先看下源码，要知其所以然。

#### 1.首先看下ProviderManager类,之前的文章已经对该类进行过分析，侧重点不太一样，对AuthenticationProvider不懂的可以翻看其它文章
```
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		Class<? extends Authentication> toTest = authentication.getClass();
		AuthenticationException lastException = null;
		AuthenticationException parentException = null;
		Authentication result = null;
		Authentication parentResult = null;
		boolean debug = logger.isDebugEnabled();

		for (AuthenticationProvider provider : getProviders()) {
			if (!provider.supports(toTest)) {
				continue;
			}

			if (debug) {
				logger.debug("Authentication attempt using "
						+ provider.getClass().getName());
			}

			try {
			    //调用用户自定义的认证处理方法
				result = provider.authenticate(authentication);

				if (result != null) {
					copyDetails(authentication, result);
					break;
				}
			}
			catch (AccountStatusException e) {
			    //重点，调用认证失败发布事件方法
				prepareException(e, authentication);
				// SEC-546: Avoid polling additional providers if auth failure is due to
				// invalid account status
				throw e;
			}
			catch (InternalAuthenticationServiceException e) {
			    //重点，调用认证失败发布事件方法
				prepareException(e, authentication);
				throw e;
			}
			catch (AuthenticationException e) {
				lastException = e;
			}
		}

		if (result == null && parent != null) {
			// Allow the parent to try.
			try {
				result = parentResult = parent.authenticate(authentication);
			}
			catch (ProviderNotFoundException e) {
				// ignore as we will throw below if no other exception occurred prior to
				// calling parent and the parent
				// may throw ProviderNotFound even though a provider in the child already
				// handled the request
			}
			catch (AuthenticationException e) {
				lastException = parentException = e;
			}
		}

		if (result != null) {
			if (eraseCredentialsAfterAuthentication
					&& (result instanceof CredentialsContainer)) {
				// Authentication is complete. Remove credentials and other secret data
				// from authentication
				((CredentialsContainer) result).eraseCredentials();
			}

			// If the parent AuthenticationManager was attempted and successful than it will publish an AuthenticationSuccessEvent
			// This check prevents a duplicate AuthenticationSuccessEvent if the parent AuthenticationManager already published it
			if (parentResult == null) {
			    //重点，调用认证成功发布事件方法
				eventPublisher.publishAuthenticationSuccess(result);
			}
			return result;
		}
		private void prepareException(AuthenticationException ex, Authentication auth) {
		        //发布失败事件
        		eventPublisher.publishAuthenticationFailure(ex, auth);
        	}
```    
发布事件对象eventPublisher是一个AuthenticationEventPublisher类型，它是一个接口，里面有如下两个方法：
```
public interface AuthenticationEventPublisher {
    //发布认证成功事件
	void publishAuthenticationSuccess(Authentication authentication);
    //发布认证失败事件
	void publishAuthenticationFailure(AuthenticationException exception,
			Authentication authentication);
}
```
我们在ProviderManager类中看到eventPublisher的初始化值是一个NullEventPublisher对象，但是NullEventPublisher类中没有做任何的处理，所以推测肯定有其它地方对eventPublisher
进行初始化，ProviderManager类中有一个setAuthenticationEventPublisher方法：
```
	public void setAuthenticationEventPublisher(
			AuthenticationEventPublisher eventPublisher) {
		Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
		this.eventPublisher = eventPublisher;
	}
```
上面的方法就是初始化事件发布对象的入口，这个方法是由AuthenticationManagerBuilder类来进行初始化的

#### 2.AuthenticationManagerBuilder类分析
```
	@Override
	protected ProviderManager performBuild() throws Exception {
		if (!isConfigured()) {
			logger.debug("No authenticationProviders and no parentAuthenticationManager defined. Returning null.");
			return null;
		}
		ProviderManager providerManager = new ProviderManager(authenticationProviders,
				parentAuthenticationManager);
		if (eraseCredentials != null) {
			providerManager.setEraseCredentialsAfterAuthentication(eraseCredentials);
		}
		if (eventPublisher != null) {
		    //初始化发布事件
			providerManager.setAuthenticationEventPublisher(eventPublisher);
		}
		providerManager = postProcess(providerManager);
		return providerManager;
	}
```
看到上面的源码了没，就是在这里对ProviderManager类中的发布事件进行初始化的；继续查看代码发现AuthenticationManagerBuilder类中存在eventPublisher对象及初始化方法，即：
```
private AuthenticationEventPublisher eventPublisher;
private AuthenticationEventPublisher eventPublisher;
	public AuthenticationManagerBuilder authenticationEventPublisher(
			AuthenticationEventPublisher eventPublisher) {
		Assert.notNull(eventPublisher, "AuthenticationEventPublisher cannot be null");
		this.eventPublisher = eventPublisher;
		return this;
	}
```
问题又回到了类似1的问题，是由哪个类调用AuthenticationManagerBuilder的authenticationEventPublisher初始化方法，接下来看下WebSecurityConfigurerAdapter类

#### 3.WebSecurityConfigurerAdapter类分析
```
	public void init(final WebSecurity web) throws Exception {
		final HttpSecurity http = getHttp();
		web.addSecurityFilterChainBuilder(http).postBuildAction(new Runnable() {
			public void run() {
				FilterSecurityInterceptor securityInterceptor = http
						.getSharedObject(FilterSecurityInterceptor.class);
				web.securityInterceptor(securityInterceptor);
			}
		});
	}
```
上面的init方法调用了getHttp方法
```
	protected final HttpSecurity getHttp() throws Exception {
		if (http != null) {
			return http;
		}
        //创建DefaultAuthenticationEventPublisher实例对象
		DefaultAuthenticationEventPublisher eventPublisher = objectPostProcessor
				.postProcess(new DefaultAuthenticationEventPublisher());
		//设置AuthenticationManagerBuilder对象的事件发布对象
		localConfigureAuthenticationBldr.authenticationEventPublisher(eventPublisher);

		AuthenticationManager authenticationManager = authenticationManager();
		authenticationBuilder.parentAuthenticationManager(authenticationManager);
		authenticationBuilder.authenticationEventPublisher(eventPublisher);
		Map<Class<? extends Object>, Object> sharedObjects = createSharedObjects();

		...
	}    
```
上面的源码已经注解了，事件发布的真实对象就是DefaultAuthenticationEventPublisher，它是AuthenticationEventPublisher接口的实现，接下来看下源码分析

#### 4.AuthenticationEventPublisher源码分析
```
public class DefaultAuthenticationEventPublisher implements AuthenticationEventPublisher,
		ApplicationEventPublisherAware {
	...
    //添加事件子类到集合中
	public DefaultAuthenticationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;

		addMapping(BadCredentialsException.class.getName(),
				AuthenticationFailureBadCredentialsEvent.class);
		addMapping(UsernameNotFoundException.class.getName(),
				AuthenticationFailureBadCredentialsEvent.class);
		addMapping(AccountExpiredException.class.getName(),
				AuthenticationFailureExpiredEvent.class);
		addMapping(ProviderNotFoundException.class.getName(),
				AuthenticationFailureProviderNotFoundEvent.class);
		addMapping(DisabledException.class.getName(),
				AuthenticationFailureDisabledEvent.class);
		addMapping(LockedException.class.getName(),
				AuthenticationFailureLockedEvent.class);
		addMapping(AuthenticationServiceException.class.getName(),
				AuthenticationFailureServiceExceptionEvent.class);
		addMapping(CredentialsExpiredException.class.getName(),
				AuthenticationFailureCredentialsExpiredEvent.class);
		addMapping(
				"org.springframework.security.authentication.cas.ProxyUntrustedException",
				AuthenticationFailureProxyUntrustedEvent.class);
	}
    //发布认证成功方法
	public void publishAuthenticationSuccess(Authentication authentication) {
		if (applicationEventPublisher != null) {
		    //真实发布认证成功事件
			applicationEventPublisher.publishEvent(new AuthenticationSuccessEvent(
					authentication));
		}
	}
    //发布认证失败方法
	public void publishAuthenticationFailure(AuthenticationException exception,
			Authentication authentication) {
		Constructor<? extends AbstractAuthenticationEvent> constructor = exceptionMappings
				.get(exception.getClass().getName());
		AbstractAuthenticationEvent event = null;

		if (constructor != null) {
			try {
				event = constructor.newInstance(authentication, exception);
			}
			catch (IllegalAccessException ignored) {
			}
			catch (InstantiationException ignored) {
			}
			catch (InvocationTargetException ignored) {
			}
		}

		if (event != null) {
			if (applicationEventPublisher != null) {
			    //真实发布认证失败事件
				applicationEventPublisher.publishEvent(event);
			}
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("No event was found for the exception "
						+ exception.getClass().getName());
			}
		}
	}

    ...
}
```
到这里Spring Security中的事件发布采用倒叙的方式已经将源码分析完了，具体的代码实现将在下一篇文章中讲解；

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgrade.md](https://github.com/mingyang66/spring-parent/blob/master/spring-security-oauth2-server-redis-service/eventUpgrade.md)


    
    