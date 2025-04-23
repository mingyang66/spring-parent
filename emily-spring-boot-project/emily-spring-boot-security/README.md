加解密SDK使用手册

- pom依赖

```xml
            <dependency>
                <groupId>com.eastmoney.emis</groupId>
                <artifactId>emily-spring-boot-security</artifactId>
                <version>${revision}</version>
            </dependency>
```

- 新增加解密注解

| 注解               | 描述                   |
| ------------------ | ---------------------- |
| @SecurityOperation | 标记在控制器方法上     |
| @SecurityModel     | 标记在实体类上         |
| @SecurityProperty  | 标记在字符串属性字段上 |

- 提供两个基于BasePlugin的扩展插件基类

| 插件类名              | 描述                     |
| --------------------- | ------------------------ |
| SimpleSecurityPlugin  | 简单字符串类型属性加解密 |
| ComplexSecurityPlugin | 复杂实体类类型字段加解密 |

- 枚举类SecurityType

| 属性名   | 描述                           |
| -------- | ------------------------------ |
| REQUEST  | 对请求入参实体类字段进行加解密 |
| RESPONSE | 对请求响应实体类进行加解密     |

- 基于SimpleSecurityPlugin插件基类使用案例

定义加解密插件实现类：

```java
@Component
public class SimpleUserEncryPlugin implements SimpleSecurityPlugin<String> {
    @Override
    public String getPlugin(String value) {
        return value + "-加解";
    }
}
```

定义入参实体类：

```java
@SecurityModel
public class UserReq {
    @SecurityProperty(value = UserSimplePlugin.class)
    public String city;
}
```

定义出参实体类：

```java
@SecurityModel
public class UserRes {
    @SecurityProperty(value = UserSimplePlugin.class)
    public String city;
}
```

定义对请求入参和返回值进行加解密处理的控制器：

```java
    @SecurityOperation(value = {SecurityType.REQUEST, SecurityType.RESPONSE})
    @PostMapping("api/request/getAddress")
    public UserRes addressReq(@Validated @RequestBody UserReq userReq) {
        UserRes userRes = new UserRes();
        userRes.city = userReq.city;
        return userRes;
    }
```

- 基于 ComplexSecurityPlugin复杂类型插件基类使用方案

插件定义跟简单模式区别是可以获取到当前实体类对象，根据改对象做复杂的操作，如对象重新赋值、根据对象中的其它字段进行加解密等：

```java
@Component
public class UserComplexSecurityPlugin implements ComplexSecurityPlugin<User, String> {
    @Override
    public String getPlugin(User user, String value) {
        user.setAge(28);
        user.setId(4567891233);
        return value + "-解密后";
    }
}
```

