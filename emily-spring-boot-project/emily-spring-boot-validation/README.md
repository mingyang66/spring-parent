Resource Bundle：[https://www.jetbrains.com/help/idea/resource-bundle.html](https://www.jetbrains.com/help/idea/resource-bundle.html)

- 读取Resource Bundle属性配置文件

```java
ResourceBundle bundle = ResourceBundle.getBundle("ValidationMessages", Locale.CHINA);
    System.out.println(bundle.getString("jakarta.validation.constraints.IsAccountCode.message"));
```

