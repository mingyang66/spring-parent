### Spring-boot2.1.6开启HTTP响应压缩（Enable HTTP Response Compression）

Jetty,Tomcat和Undertow都支持HTTP响应压缩，可以通过application.properties配置文件开启，如下：

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

excludedUserAgents属性即server.compression.excluded-user-agents的使用方法未介绍，此属性设置官网也未介绍使用方法，网上找了很多都没什么用，暂时无解。。。



官网地址：[ https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/htmlsingle/#how-to-enable-http-response-compression ]( https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/htmlsingle/#how-to-enable-http-response-compression )
GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-returnvalue-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-returnvalue-service)