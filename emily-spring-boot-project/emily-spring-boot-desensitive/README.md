##### 脱敏SDK组件

- pom依赖

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-spring-boot-desensitive</artifactId>
                <version>${revision}</version>
            </dependency>
```

- 注解列表

| 注解                        | 作用域                     | 描述                         |
| --------------------------- | -------------------------- | ---------------------------- |
| @DesensitizeOperation       | 标记在类、方法上           | 标记此注解拦截器才会拦截处理 |
| @DesensitizeModel           | 标记在实体类上             | 标记的实体类才会脱敏处理     |
| @DesensitizeProperty        | 标记在实体类字符串属性字段 | 标记的字段才会脱敏处理       |
| @DesensitizeNullProperty    | 标记在实体类引用数据类型上 | 标记的引用字段会置为null     |
| @DesensitizeMapProperty     | 标记在实体类Map数据类型上  |                              |
| @DesensitizeComplexProperty |                            |                              |

