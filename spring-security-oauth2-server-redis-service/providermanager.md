### Spring Security之ProviderManager源码浅析

#### 1.AuthenticationManager类源码解析
```
public interface AuthenticationManager {
	Authentication authenticate(Authentication authentication)
			throws AuthenticationException;
}
```
AuthenticationManager是一个顶级接口，用来处理身份验证请求，并返回一个Authentication对象，如果发生异常将会抛出AuthenticationException；
AuthenticationManager的实现有很多，通常使用ProviderManager对认证请求进行管理；

ProviderManager主要是对AuthenticationProvider进项管理，看下注解描述：
```
 * <tt>AuthenticationProvider</tt>s are usually tried in order until one provides a
 * non-null response. A non-null response indicates the provider had authority to decide
 * on the authentication request and no further providers are tried. If a subsequent
 * provider successfully authenticates the request, the earlier authentication exception
 * is disregarded and the successful authentication will be used. If no subsequent
 * provider provides a non-null response, or a new <code>AuthenticationException</code>,
 * the last <code>AuthenticationException</code> received will be used. If no provider
 * returns a non-null response, or indicates it can even process an
 * <code>Authentication</code>, the <code>ProviderManager</code> will throw a
 * <code>ProviderNotFoundException</code>. A parent {@code AuthenticationManager} can also
 * be set, and this will also be tried if none of the configured providers can perform the
 * authentication. This is intended to support namespace configuration options though and
 * is not a feature that should normally be required.
 ```
 >TIPS:AuthenticationProvider通常按照顺序去执行，一个返回非null响应表示程序验证通过，不再尝试验证其它的provider;如果后续提供的身份验证程序
 成功地对请求进行身份认证，则忽略先前的身份验证异常及null响应，并将使用成功的身份验证。如果没有provider提供一个非null响应，或者有一个新的抛出AuthenticationException，
 那么最后的AuthenticationException将会抛出。
 
ProviderManager中有一个List用来存储定义的AuthenticationProvider认证实现类，也可以认为是一个认证处理器链来支持同一个应用中的多个不同身份认证机制，ProviderManager将会根据顺序来进行验证
 ```
 private List<AuthenticationProvider> providers = Collections.emptyList();
 ```
ProviderManager类继承AuthenticationManager接口，实现了authenticate方法
 ```
 	public Authentication authenticate(Authentication authentication)
 			throws AuthenticationException {
 		Class<? extends Authentication> toTest = authentication.getClass();
 		AuthenticationException lastException = null;
 		AuthenticationException parentException = null;
 		Authentication result = null;
 		Authentication parentResult = null;
 		boolean debug = logger.isDebugEnabled();
        //获取AuthenticationProvider对象，循环遍历
 		for (AuthenticationProvider provider : getProviders()) {
 		    //如果支持认证实现类就继续处理
 			if (!provider.supports(toTest)) {
 				continue;
 			}
 
 			if (debug) {
 				logger.debug("Authentication attempt using "
 						+ provider.getClass().getName());
 			}
 
 			try {
 			    //重点，调用实现类的authenticate方法进行真实业务逻辑认证处理
 				result = provider.authenticate(authentication);
 
 				if (result != null) {
 					copyDetails(authentication, result);
 					break;
 				}
 			}
 			catch (AccountStatusException e) {
 				prepareException(e, authentication);
 				// SEC-546: Avoid polling additional providers if auth failure is due to
 				// invalid account status
 				throw e;
 			}
 			catch (InternalAuthenticationServiceException e) {
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
 			    //发送认证成功事件
 				eventPublisher.publishAuthenticationSuccess(result);
 			}
 			return result;
 		}
 
 		// Parent was null, or didn't authenticate (or throw an exception).
 
 		if (lastException == null) {
 			lastException = new ProviderNotFoundException(messages.getMessage(
 					"ProviderManager.providerNotFound",
 					new Object[] { toTest.getName() },
 					"No AuthenticationProvider found for {0}"));
 		}
 
 		// If the parent AuthenticationManager was attempted and failed than it will publish an AbstractAuthenticationFailureEvent
 		// This check prevents a duplicate AbstractAuthenticationFailureEvent if the parent AuthenticationManager already published it
 		if (parentException == null) {
 			prepareException(lastException, authentication);
 		}
 
 		throw lastException;
 	}
 ```

AuthenticationProvider是一个顶级的接口，里面只提供了两个方法
```
public interface AuthenticationProvider {
	Authentication authenticate(Authentication authentication) throws AuthenticationException;
	boolean supports(Class<?> authentication);
}
```
>TIPS:AuthenticationProvider接口和AuthenticationManager接口很相似，只多了一个supports方法，它是用来验证是否支持某种身份验证方式；该接口通常是提供给开发人员
实现，按照自己系统的特点来进行扩展验证
