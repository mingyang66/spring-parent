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
------
### 自动化配置组件AutoConfiguration
##### 1.关闭Redisson组件
```
@SpringBootApplication(exclude = {RedissonAutoConfiguration.class})
```

##### 2.关闭拦截控制器日志组件

```
@SpringBootApplication(exclude = {InterceptorAutoConfiguration.class})
```

##### 3.关闭RestTemplate网络请求配置组件

```
@SpringBootApplication(exclude = {HttpClientAutoConfiguration.class})
```

##### 4.关闭Redis配置组件

```
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

##### 5.关闭控制器返回值包装组件

```
@SpringBootApplication(exclude = {ReturnValueAutoConfiguration.class})
```

