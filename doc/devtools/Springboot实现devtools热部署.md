### Springboot实现devtools热部署

##### 1.添加pom依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>
```

##### 2.修改配置

- windows版：File-Settings-Compiler-Build-勾选Project automatically
- mac版idea:IntellIJ IDEA-Preference-File-Settings-Compiler-勾选Build Project automatically
- windows版idea:ctrl+shift+alt+/,选择Registry,勾选Compiler automake allow when app running
- mac版idea:option+command+shift+/,选择Registry,勾选Compiler automake allow when app running

##### 3.编译重启

- windows版：ctrl+F9如果有改动编译重启（Build-Build Project）
- mac版：fn+command+F9如果有改动就编译重启（Build-Build Project）

##### 4.官方文档说明

```
Triggering a restart

As DevTools monitors classpath resources, the only way to trigger a restart is to update the classpath. The way in which you cause the classpath to be updated depends on the IDE that you are using. In Eclipse, saving a modified file causes the classpath to be updated and triggers a restart. In IntelliJ IDEA, building the project (Build -> Build Project) has the same effect.
```

官方文档：https://docs.spring.io/spring-boot/docs/2.1.14.BUILD-SNAPSHOT/reference/html/using-boot-devtools.html

