##### 脱敏SDK组件

##### 一、pom依赖

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-spring-boot-desensitize</artifactId>
                <version>${revision}</version>
            </dependency>
```

##### 二、属性配置

```properties
#脱敏组件SDK开关
spring.emily.desensitize.enabled=true
```



##### 三、注解列表

| 注解                         | 作用域                                                       |
| ---------------------------- | ------------------------------------------------------------ |
| @DesensitizeOperation        | 标记在方法上，只有标记了此注解的返回值才会进行脱敏处理，removePackClass属性指定要剥离的外层类，可以指定多个剥离的外层类，只有最内层的类才会进行脱敏处理； |
| @DesensitizeModel            | 标记在实体类上，只有标记了此注解的实体类才会进行脱敏处理     |
| @DesensitizeProperty         | 标记在实体类字符串、Map属性字段，标记了次注解的字段会按照指定类型进行脱敏； |
| @DesensitizeNullProperty     | 标记在实体类引用数据类型上                                   |
| @DesensitizeMapProperty      | 标记在实体类Map数据类型上，按照指定的key字段及类型进行脱敏。 |
| @DesensitizeFlexibleProperty | 标记在实体类属性字段上，需两个字段配合使用                   |

##### 四、应用场景

- 方法上标记了@DesensitizeOperation注解的返回值对象会根据其它注解的标注情况进行脱敏处理；
- 除@DesensitizeOperation注解外，其它注解标注在入参、返回值实体类对象上日志系统会对这些进行脱敏处理，不会影响到具体的返回对象和入参对象；

##### 五、案例如下：

- 实体类Company

```java
@DesensitizeModel
public class Company {
    private String companyName;
    @DesensitizeProperty(value = DesensitizeType.ADDRESS)
    private String address;
    @DesensitizeProperty(value = DesensitizeType.PHONE)
    private String phone;
    @DesensitizeProperty(value = DesensitizeType.EMAIL)
    private String email;
    /**
     * {@link DesensitizeProperty}注解和{@link DesensitizeMapProperty} 注解都可以对Map集合中value为String的值进行脱敏处理；
     * {@link DesensitizeMapProperty}注解优先级高于{@link DesensitizeProperty}注解
     */
    @DesensitizeProperty
    @DesensitizeMapProperty(keys = {"password", "username"}, types = {DesensitizeType.DEFAULT, DesensitizeType.USERNAME})
    private Map<String, Object> dataMap = new HashMap<>();
    @DesensitizeProperty
    private List<String> list;
    @DesensitizeProperty
    private String[] arrays;
    /**
     * 将任何引用类型字段设置为null,且优先级最高
     */
    @DesensitizeNullProperty
    private Double testNull;
    /**
     * 复杂字段脱敏处理，根据传入的字段key值判断对应字段value是否进行脱敏处理
     */
    @DesensitizeFlexibleProperty(value = {"email", "phone"}, target = "fieldValue", desensitizeType = {DesensitizeType.EMAIL, DesensitizeType.PHONE})
    private String fieldKey;
    private String fieldValue;
    }
```

- 返回值是实体类，会对实体类进行脱敏处理

```java
    @DesensitizeOperation
    @GetMapping("api/desensitize/getCompany")
    public Company getCompany() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("email");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return company;
    }
```

- 返回字符串，不支持

```java
    @DesensitizeOperation
    @GetMapping("api/desensitize/getCompanyStr")
    public String getCompanyStr() {
        return "魔方科技";
    }
```



- 返回值是List集合对象，会对内层实体类脱敏处理

```java
    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyList")
    public ResponseEntity<List<Company>> getCompanyList() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("email");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return ResponseEntity.ok(List.of(company));
    }

```

- 返回List字符串，不支持

```java
    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyListStr")
    public ResponseEntity<List<String>> getCompanyListStr() {
        return ResponseEntity.ok(List.of("古北市南京路1688号50号楼106"));
    }
```



- 返回值是Map集合，会对内层实体类脱敏处理

```java
    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyMap")
    public ResponseEntity<Map<String, Company>> getCompanyMap() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("email");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return ResponseEntity.ok(Map.of("test", company));
    }
```

- 返回Map字符串集合，不支持

```java
    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyMapStr")
    public ResponseEntity<Map<String, String>> getCompanyMapStr() {
        return ResponseEntity.ok(Map.of("test", "魔方科技"));
    }
```



- 返回值是数组类型集合，会对内层实体类进行脱敏处理

```java
    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyArray")
    public ResponseEntity<Company[]> getCompanyArray() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("email");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return ResponseEntity.ok(new Company[]{company});
    }
```

- 返回字符串数组

```java
    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyArrayStr")
    public ResponseEntity<String[]> getCompanyArrayStr() {
        return ResponseEntity.ok(new String[]{"魔方科技"});
    }
```



- 返回值带有外层包装，指定剥离外层的实体类，会对内层类进行脱敏处理

```java

    @DesensitizeOperation(removePackClass = {BaseResponse.class, ResponseEntity.class, ResponseEntity.class})
    @GetMapping("api/desensitize/getCompanyPack")
    public BaseResponse<ResponseEntity<ResponseEntity<Company>>> getCompanyPack() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        BaseResponse<ResponseEntity<ResponseEntity<Company>>> baseResponse = new BaseResponse<>();
        baseResponse.setData(ResponseEntity.ok(ResponseEntity.ok(company)));
        company.setTestNull(100D);
        company.setFieldKey("phone");
        company.setFieldValue("188888888888");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return baseResponse;
    }
```

