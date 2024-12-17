##### 一、POM依赖

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-spring-boot-i18n</artifactId>
                <version>${revision}</version>
            </dependency>
```

##### 二、属性配置

```properties
# 多语言配置组件开关
spring.emily.i18n.enabled=true
```

##### 三、注释

- @I18nOperation 标记在控制器返回方法上，只有标记了此注解的方法拦截器才会处理，才会对返回值的字段进行多语言翻译；如果返回值为Collection、Map、数组都支持会自动解析内层实体类；
- @I18nModel 标记在返回值实体类上，如果有嵌套的实体类也需要标记此注解，否则不会进行翻译；
- @I18nProperty 标记在实体类的String属性字段上，只有字符串有效，其它类型无效；

##### 四、案例

- 实体类定义

```java
@I18nModel
public class Bank {
    @I18nProperty
    private String name;
    @I18nProperty
    private String code;

    private SubBank subBank;

    public SubBank getSubBank() {
        return subBank;
    }

    public void setSubBank(SubBank subBank) {
        this.subBank = subBank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @I18nModel
    public static class SubBank {
        @I18nProperty
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

```

- 返回值是实体类，会对实体类进行翻译

```java
@RestController
public class BankController {
  	/**
		* 返回值是实体类，会对实体类进行翻译
		**/
    @I18nOperation
    @GetMapping("api/i18n/getBank")
    public Bank getBank() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return bank;
    }
}
```

- 返回值是字符串，会对字符串进行翻译

```java
@RestController
public class BankController {
		/**
		* 返回值是字符串，会对字符串进行翻译
		**/
    @I18nOperation
    @GetMapping("api/i18n/getStr")
    public String getStr() {
        return "古北";
    }
}
```

- 返回值是List集合，会对实体类进行翻译

```java
    @I18nOperation
    @GetMapping("api/i18n/getBankList")
    public List<Bank> getBankList() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return List.of(bank);
    }
```

- 返回值是List字符串集合，不支持

```java
    @I18nOperation
    @GetMapping("api/i18n/getBankListStr")
    public List<String> getBankListStr() {
        return List.of("古北", "渣渣银行");
    }
```



- 返回值是Map集合，会对返回值进行翻译

```java
    @I18nOperation
    @GetMapping("api/i18n/getBankMap")
    public Map<String, Bank> getBankMap() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return Map.of("test1", bank);
    }
```

- 返回值是Map集合，不支持

```java
    @I18nOperation
    @GetMapping("api/i18n/getBankMapStr")
    public Map<String, String> getBankMapStr() {
        return Map.of("test1", "古北", "test2", "渣渣银行");
    }
```



- 返回值是数组，会对返回值进行翻译

```java
    @I18nOperation
    @GetMapping("api/i18n/getBankArray")
    public Bank[] getBankArray() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return new Bank[]{bank};
    }
```

- 返回值是数组字符串，不支持

```java
    @I18nOperation
    @GetMapping("api/i18n/getBankArrayStr")
    public String[] getBankArrayStr() {
        return new String[]{"古北", "渣渣银行"};
    }
```

