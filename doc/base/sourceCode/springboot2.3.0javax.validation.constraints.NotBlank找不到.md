### springboot2.3.0 javax.validation.constraints.NotBlank找不到异常

> 原因是2.3.0的spring-boot-starter-web没有引入validation对应的包

查看2.3.0以前的版本，以2.2.7版本为例spring-boot-starter-web-2.2.7.RELEASE.pom：

```
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
      <version>2.2.7.RELEASE</version>
      <scope>compile</scope>
      <exclusions>
        <exclusion>
          <artifactId>tomcat-embed-el</artifactId>
          <groupId>org.apache.tomcat.embed</groupId>
        </exclusion>
      </exclusions>
```

而查询spring-boot-starter-web-2.3.0.RELEASE.pom没有validation对应的包，所以要想使用校验功能要手动引入包：

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)