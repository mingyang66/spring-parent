# spring-parent
### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
mvn versions:set -DnewVersion=2.1.1.RELEASE
```

##### 2 撤销设置

```
mvn versions:revert
```

##### 3 提交设置

```
mvn versions:commit
```
##### 4.项目打包(同时处理项目所依赖的包)

```
mvn clean install -pl sgrain-spring-boot-starter -am
```
或
```
./mvnw clean install -pl sgrain-spring-boot-starter -am
```



| 参数 | 全程                   | 说明                                                         |
| ---- | ---------------------- | ------------------------------------------------------------ |
| -pl  | --projects             | 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔) |
| -am  | --also-make            | 表示同时处理选定模块所依赖的模块                             |
| -amd | --also-make-dependents | 表示同时处理依赖选定模块的模块                               |
| -N   | --non-                 | 表示不递归子模块                                             |
| -rf  | --resume-frm           | 表示从指定模块开始继续处理                                   |

### 打tag标签

##### 1.添加tag

```
git tag -a version1.0 -m 'first version'
```

##### 2.提交tag

```
git push origin --tags
```

其它tag操作参考：[tag操作指南](https://blog.csdn.net/yaomingyang/article/details/78839295?ops_request_misc=%7B%22request%5Fid%22%3A%22158685673019724835840750%22%2C%22scm%22%3A%2220140713.130056874..%22%7D&request_id=158685673019724835840750&biz_id=0&utm_source=distribute.pc_search_result.none-task-blog-blog_SOOPENSEARCH-1)

------
### 自研框架-sgrain(小米粒)配置
```java
#设置开启用户请求日志拦截器模式，默认true
spring.sgrain.log.enable=true
#设置开启日志debug模式，默认true
spring.sgrain.log.debug=true

#设置开启返回结果包装，默认true
spring.sgrain.return-value.enable=true
#设置https配置开关,默认false
spring.sgrain.https.enable=false
#RedisTemplate组件开关，默认true
spring.sgrain.redis.enable=true
#限流组件开关，默认true
spring.sgrain.rate-limit.enable=true
#防止重复提交组件开关，默认true
spring.sgrain.idempotent.enable=true

#Http RestTemplate组件开关，默认true
spring.sgrain.http-client.enable=true
#http连接读取超时时间，默认5000毫秒
spring.sgrain.http-client.read-time-out=5000
#http连接连接超时时间，默认10000毫秒
spring.sgrain.http-client.connect-time-out=10000

##API路由设置
#是否开启所有接口的前缀prefix,默认前面添加api
spring.sgrain.web.path.enable-all-prefix=true
#自定义添加前缀
spring.sgrain.web.path.prefix=api
#区分大小写
spring.sgrain.web.path.case-sensitive=false
#是否缓存匹配规则,默认null等于true
spring.sgrain.web.path.cache-patterns=true
#是否去除前后空格,默认false
spring.sgrain.web.path.trim-tokens=false
#设置URL末尾是否支持斜杠，默认true,如/a/b/有效，/a/b也有效
spring.sgrain.web.path.use-trailing-slash-match=true
#忽略URL前缀控制器设置,默认空
spring.sgrain.web.path.ignore-controller-url-prefix=springfox.documentation.swagger.web.ApiResourceController

##跨域设置
#开启跨域设置，默认false
spring.sgrain.web.cors.enable=true
#设置允许哪些源来访问
spring.sgrain.web.cors.allowed-origins=http://www.baidu.com,http://www.bubuko.com
#允许HTTP请求方法
spring.sgrain.web.cors.allowed-methods=GET,POST
#设置用户可以拿到的字段
spring.sgrain.web.cors.allowed-headers=
#设置浏览器是否应该发送凭据cookie
spring.sgrain.web.cors.allow-credentials=true
#设置响应HEAD,默认无任何设置，不可以使用*号
spring.sgrain.web.cors.exposed-headers=
#设置多长时间内不需要发送预检验请求，可以缓存该结果，默认1800秒
spring.sgrain.web.cors.max-age=1800


##swagger配置
spring.sgrain.swagger.enable=true
#分组，使用英文单词，逗号隔开；如：group1,group2,group3
spring.sgrain.swagger.group=sgrain,rabbit
#分组名称，使用逗号隔开,跟group一一对应；如：groupName1,groupName2,groupName3
spring.sgrain.swagger.group-name=小米粒,RabbitMQ测试
#扫描包，使用逗号隔开；如：com.sgrain.boot,com.sgrain.test
spring.sgrain.swagger.base-package=com.yaomy.control.test.api.rabbit,com.yaomy.control.test.api.sgrain
#标题
spring.sgrain.swagger.api-info.title=Springboot2.3.0 API接口文档
#描述
spring.sgrain.swagger.api-info.description=小米粥是以小米作为主要食材熬制而成的一种独具特色的北方粥点，口味清淡，清香味，具有简单易制，健胃消食的特点。煮粥时一定要先烧开水然后放入洗净后的小米，先煮沸，然后用文火熬，汤粘稠后即可关火。
#版本号
spring.sgrain.swagger.api-info.version=V2.1.3.RELEASE
```

