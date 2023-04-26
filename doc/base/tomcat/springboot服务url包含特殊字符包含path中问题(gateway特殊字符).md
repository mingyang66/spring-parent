### springboot服务url包含特殊字符包含path中问题(gateway特殊字符)

> 我们使用springboot开发接口提供给端上时，url路径或者参数中都可能包含特殊未编码的字符，报400 bad request，导致请求无法正常处理。

##### 针对tomcat服务器可以通过配置解决

```properties
#参数中允许未编码的特殊字符
server.tomcat.relaxed-query-chars=^,|
#请求URL中允许位编码的特殊字符
server.tomcat.relaxed-path-chars=^
```

##### 针对webflux或springcloud gateway请求的特殊字符

通过源码分析可以知道在解析请求参数是会抛出URISyntaxException异常

```java
/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.http.server.reactive;

import java.net.URISyntaxException;
import java.util.function.BiFunction;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.logging.Log;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/**
 * Adapt {@link HttpHandler} to the Reactor Netty channel handling function.
 *
 * @author Stephane Maldini
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class ReactorHttpHandlerAdapter implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {

	private static final Log logger = HttpLogging.forLogName(ReactorHttpHandlerAdapter.class);


	private final HttpHandler httpHandler;


	public ReactorHttpHandlerAdapter(HttpHandler httpHandler) {
		Assert.notNull(httpHandler, "HttpHandler must not be null");
		this.httpHandler = httpHandler;
	}


	@Override
	public Mono<Void> apply(HttpServerRequest reactorRequest, HttpServerResponse reactorResponse) {
		NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(reactorResponse.alloc());
		try {
			ReactorServerHttpRequest request = new ReactorServerHttpRequest(reactorRequest, bufferFactory);
			ServerHttpResponse response = new ReactorServerHttpResponse(reactorResponse, bufferFactory);

			if (request.getMethod() == HttpMethod.HEAD) {
				response = new HttpHeadResponseDecorator(response);
			}

			return this.httpHandler.handle(request, response)
					.doOnError(ex -> logger.trace(request.getLogPrefix() + "Failed to complete: " + ex.getMessage()))
					.doOnSuccess(aVoid -> logger.trace(request.getLogPrefix() + "Handling completed"));
		}
		catch (URISyntaxException ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to get request URI: " + ex.getMessage());
			}
			reactorResponse.status(HttpResponseStatus.BAD_REQUEST);
			return Mono.empty();
		}
	}

}

```

暂时没有找到很好的解决方案，可以通过覆盖ReactorServerHttpRequest类的方式来解决

```java
    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull(request, "HttpServerRequest must not be null");
        String resolveRequestUri = resolveRequestUri(request);
        if(resolveRequestUri.contains("^")){
            resolveRequestUri = resolveRequestUri.replace("^", "%5E");
        }
        if(resolveRequestUri.contains("|")){
            resolveRequestUri = resolveRequestUri.replace("|", "%7C");
        }
        if(resolveRequestUri.contains("{")){
            resolveRequestUri = resolveRequestUri.replace("{", "%7B");
        }
        if(resolveRequestUri.contains("}")){
            resolveRequestUri = resolveRequestUri.replace("}", "%7D");
        }
        return new URI(resolveBaseUrl(request) + resolveRequestUri);
    }
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

Gateway网关实例：[https://github.com/mingyang66/EmilyGateway](https://github.com/mingyang66/EmilyGateway)