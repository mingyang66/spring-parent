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

| 注解                        | 作用域                                                       |
| --------------------------- | ------------------------------------------------------------ |
| @DesensitizeOperation       | 标记在类、方法上，表示对方法返回值进行脱敏处理，拦截器会对方法访问进行拦截 |
| @DesensitizeModel           | 标记在实体类上，只有标记了此注解的实体类才会进行脱敏处理     |
| @DesensitizeProperty        | 标记在实体类字符串、Map属性字段，标记了次注解的字段会按照指定类型进行脱敏； |
| @DesensitizeNullProperty    | 标记在实体类引用数据类型上                                   |
| @DesensitizeMapProperty     | 标记在实体类Map数据类型上，按照指定的key字段及类型进行脱敏。 |
| @DesensitizeComplexProperty | 标记在实体类属性字段上，需两个字段配合使用                   |

- @DesensitizeOperation注解标记在类、方法上，方法上优先级最高；
- @DesensitizeModel标记在实体类上，标识对当前实体类进行脱敏
- @DesensitizeNullProperty标记在任何引用数据类型上，字段值会被设置为null，优先级最高；
- @DesensitizeMapProperty标记在Map类型字段上，根据属性指定字段进行脱敏，优先级高于@DesensitizeProperty；
- @DesensitizeProperty标记在字符串类型字段上，对字段进行脱敏处理；也可以标记在Map类型字段上，对value为String的进行脱敏处理；也可以标记在Collection集合上对集合进行脱敏；也可以标记在数组上，对数组进行脱敏处理；
- @DesensitizeComplexProperty标记在复杂字段上，对当前字段值符合指定值的，对指定的另外一个字段值进行脱敏处理，具体使用方法可以看下面的案例；

#### 案例如下：

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
    @DesensitizeComplexProperty(keys = {"email", "phone"}, value = "fieldValue", types = {DesensitizeType.EMAIL, DesensitizeType.PHONE})
    private String fieldKey;
    private String fieldValue;
    }
```

- 控制器

```java
@DesensitizeOperation(removePackClass = ResponseEntity.class)
@RestController
@RequestMapping("api/desensitize")
public class DesensitizeController {
    @DesensitizeOperation
    @GetMapping("getCompany")
    public Company getCompany() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("test");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return company;
    }

    @GetMapping("getCompany1")
    public ResponseEntity<Company> getCompany1() {
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
        return ResponseEntity.ok(company);
    }

    @DesensitizeOperation(removePackClass = BaseResponse.class)
    @GetMapping("getCompany2")
    public BaseResponse<Company> getCompany2() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        BaseResponse<Company> baseResponse = new BaseResponse<>();
        baseResponse.setData(company);
        company.setTestNull(100D);
        company.setFieldKey("phone");
        company.setFieldValue("188888888888");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return baseResponse;
    }
}
```

