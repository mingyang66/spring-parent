### Spring-boot2开启HTTP响应压缩（Enable HTTP Response Compression）

Jetty,Tomcat和Undertow都支持HTTP GZIP压缩，可以通过application.properties配置文件开启，如下：

```
server.compression.enabled=true
```

默认情况下，响应的长度必须至少为2048字节才能执行压缩。可以通过执行如下属性来配置此行为：

```
#单位字节Byte
server.compression.min-response-size=2048
```

默认情况下，只有当响应的内容类型是一下类型之一时，才会压缩响应：

- `text/html`
- `text/xml`
- `text/plain`
- `text/css`
- `text/javascript`
- `application/javascript`
- `application/json`
- `application/xml`

可以通过配置 server.compression.mime-types 属性来配置此设置：

```
server.compression.mime-types=application/json,application/xml
```

查看压缩源码org.springframework.boot.web.server.Compression，如下：

```java
public class Compression {
    private boolean enabled = false;
    private String[] mimeTypes = new String[]{"text/html", "text/xml", "text/plain", "text/css", "text/javascript", "application/javascript", "application/json", "application/xml"};
    private String[] excludedUserAgents = null;
    private DataSize minResponseSize = DataSize.ofKilobytes(2L);
    ...
```

excludedUserAgents属性即server.compression.excluded-user-agents配置属性，指定不压缩的user-agent,使用正则表达式指定哪些浏览器不压缩，如：.
*Chrome/83.0.4103.116.*|.*Safari/605.1.15.*

```java
#是否开启压缩，默认false
server.compression.enabled=true
#执行压缩的阀值，默认2048,单位：字节B
server.compression.min-response-size=2048
#指定要压缩的MIME type,多个以逗号分隔,[text/html, text/xml, text/plain, text/css, text/javascript, application/javascript, application/json, application/xml]
server.compression.mime-types=application/json
#指定不压缩的user-agent,使用正则表达式指定哪些浏览器不压缩，如：.*Chrome/83.0.4103.116.*|.*Safari/605.1.15.*
server.compression.excluded-user-agents=.*Chrome/83.0.4103.116.*|.*Safari/605.1.15.*
```

```
源码中虽然说server.compression.excluded-user-agents可以使用逗号隔开配置多个，但是实际测试发现是不支持的，如果需要配置多个可以使用正则表达式的或关系
```

##### 源码简单分析，以下是初始化tomcat容器支持压缩的基本分析：

```
org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#onRefresh
org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext#createWebServer
```

```
private void createWebServer() {
		  ...
			//获取tomcat容器对象
			this.webServer = factory.getWebServer(getSelfInitializer());
			...
	}
```

```
@Override
public WebServer getWebServer(ServletContextInitializer... initializers) {
   ...
   //初始化Connector容器
   customizeConnector(connector);
   ...
}
```

```
	protected void customizeConnector(Connector connector) {
		...
		//创建容器压缩支持
TomcatConnectorCustomizer compression = new CompressionConnectorCustomizer(getCompression());
		compression.customize(connector);
		...
	}
```

在TomcatConnectorCustomizer类中可以看到如下方法：

```java
	private void customize(AbstractHttp11Protocol<?> protocol) {
		Compression compression = this.compression;
		protocol.setCompression("on");
		protocol.setCompressionMinSize(getMinResponseSize(compression));
		protocol.setCompressibleMimeType(getMimeTypes(compression));
		if (this.compression.getExcludedUserAgents() != null) {
			protocol.setNoCompressionUserAgents(getExcludedUserAgents());
		}
	}
```

该方法获取配置文件中的属性配置，并初始化AbstractHttp11Protocol对象。

##### 调用接口时判定当前请求的浏览器获取的数据是否需要压缩源码分析

```
org.apache.coyote.CompressionConfig#useCompression
```

以下是useCompression方法源码：

```java
public boolean useCompression(Request request, Response response) {
        ...
        // If force mode, the browser checks are skipped
        if (compressionLevel != 2) {
            // Check for incompatible Browser 获取属性配置的正则表达式值
            Pattern noCompressionUserAgents = this.noCompressionUserAgents;
            if (noCompressionUserAgents != null) {
               //获取浏览器的user-agent
                MessageBytes userAgentValueMB = request.getMimeHeaders().getValue("user-agent");
                if(userAgentValueMB != null) {
                    String userAgentValue = userAgentValueMB.toString();
                  	//正则匹配，匹配成功则不进行压缩
                    if (noCompressionUserAgents.matcher(userAgentValue).matches()) {
                        return false;
                    }
                }
            }
        }

        // All checks have passed. Compression is enabled.

        // Compressed content length is unknown so mark it as such.
        response.setContentLength(-1);
        // Configure the content encoding for compressed content
        responseHeaders.setValue("Content-Encoding").setString("gzip");

        return true;
    }
```

官网地址：[ https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/htmlsingle/#how-to-enable-http-response-compression ]( https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/htmlsingle/#how-to-enable-http-response-compression )
GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/doc/returnvalue](https://github.com/mingyang66/spring-parent/tree/master/doc/returnvalue)