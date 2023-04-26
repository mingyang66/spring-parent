### jackson序列化和反序列化@JsonProperty、@JsonGetter、@JsonSetter、@JsonFormat

##### 1.@JsonProperty将传递过来的属性值序列化为指定的属性名

```java
package com.yaomy.control.test.po;


import com.fasterxml.jackson.annotation.JsonProperty;

public class People {
    @JsonProperty("USERNAME")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

```

> 前端传递过来的参数是{"USERNAME":"123"
>
}可以将json字符串反序列化为People实体类，并将属性USERNAME得值反序列化为username的值；但是此时如果将People实体对象序列化为json字符串，字符串结果为{"
> USERNAME":"123"},这个应该不是我们想要的结果，我们想要的结果是{"username":"123"},那这样如何实现呢？看下面示例：

```java
package com.yaomy.control.test.po;


import com.fasterxml.jackson.annotation.JsonProperty;

public class People {
    
    private String username;

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("USERNAME")
    public void setUsername(String username) {
        this.username = username;
    }
}
```

> 将@JsonProperty放在getter和setter方法上，setter方法是用作反序列化、getter用作序列化；这时反序列化时可以将{"USERNAME":"
> 123"}序列化为People对象，序列化时可以将People序列化为{"username":"123"}字符串

##### 2.@JsonGetter、@JsonSetter注解

```java
package com.yaomy.control.test.po;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class People {

    private String username;

    @JsonGetter("username")
    public String getUsername() {
        return username;
    }

    @JsonSetter("USERNAME")
    public void setUsername(String username) {
        this.username = username;
    }
}

```

> @JsonGetter和@JsonSetter注解只能用在getter和setter方法上，用来反序列化为指定字段名和序列化时为指定字段名，可以替换上面的@JsonProperty也可以被替换；

##### 3.@JsonFormat格式化

```java
package com.yaomy.control.test.po;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class People {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

```

##### 4.@JsonAnySetter和@JsonAnyGetter注解

- @JsonAnySetter注解是在反序列化时实体对象不存在对应的属性时加上该 注解，不存在的属性都会放到Map中去
- @JsonAnyGetter注解在序列化时Map加上该属性可以将map中的属性序列化为指定字符串

```
package com.yaomy.control.test.po;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.Map;

public class People {

    private String username;
    private Map<String, String> properties;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    @JsonAnyGetter
    public Map<String, String> getProperties() {
        return properties;
    }
    @JsonAnySetter
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}

```

> 首先可以将json字符串{"username":"123","age":1,"weight":23}反序列化为People对象，然后可以将People对象序列化为{"
> username":"123","age":1,"weight":23}

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/json](https://github.com/mingyang66/spring-parent/tree/master/doc/json)