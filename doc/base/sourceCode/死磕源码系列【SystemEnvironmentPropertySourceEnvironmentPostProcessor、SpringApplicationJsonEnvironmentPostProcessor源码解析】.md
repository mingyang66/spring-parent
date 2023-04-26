死磕源码系列【SystemEnvironmentPropertySourceEnvironmentPostProcessor、SpringApplicationJsonEnvironmentPostProcessor源码解析】

>
SystemEnvironmentPropertySourceEnvironmentPostProcessor类是EnvironmentPostProcessor接口的实现类，用来替换Environment环境中的systemEnvironment，systemEnvironment中最初存储的是SystemEnvironmentPropertySource类，现由实现类OriginAwareSystemEnvironmentPropertySource替换，OriginAwareSystemEnvironmentPropertySource提供了获取Origin的方法，即返回SystemEnvironmentOrigin对象。

##### 先看下SystemEnvironmentPropertySource类中存储的系统环境配置：

![在这里插入图片描述](https://img-blog![在这里插入图片描述](https://img-blog.csdnimg.cn/20201027105645530.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)
.csdnimg.cn/20201027105642289.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

> 我们可以看到里面存储的都是操作系统相关的环境变量配置

##### SystemEnvironmentPropertySourceEnvironmentPostProcessor源码：

```java
public class SystemEnvironmentPropertySourceEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

   /**
    * 后置处理器的优先级，其优先级高于SpringApplicationJsonEnvironmentPostProcessor
    * 值越小优先级越高
    */
   public static final int DEFAULT_ORDER = SpringApplicationJsonEnvironmentPostProcessor.DEFAULT_ORDER - 1;

   private int order = DEFAULT_ORDER;
	//后置处理器回调方法
   @Override
   public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
      //获取系统环境配置名，即：systemEnvironment
      String sourceName = StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME;
      //获取环境对象中的系统配置
      PropertySource<?> propertySource = environment.getPropertySources().get(sourceName);
      if (propertySource != null) {
        //替换系统环境对象中的环境配置
         replacePropertySource(environment, sourceName, propertySource);
      }
   }

   @SuppressWarnings("unchecked")
   private void replacePropertySource(ConfigurableEnvironment environment, String sourceName,
         PropertySource<?> propertySource) {
     //获取环境配置，转换为字典结构
      Map<String, Object> originalSource = (Map<String, Object>) propertySource.getSource();
     //转换为将要替换的环境对象 
     SystemEnvironmentPropertySource source = new OriginAwareSystemEnvironmentPropertySource(sourceName,
            originalSource);
     //替换环境对象
      environment.getPropertySources().replace(sourceName, source);
   }

   @Override
   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   /**
    * 为SystemEnvironmentPropertySource提供SystemEnvironmentOrigin源
    * 其Origin代表的是属性配置最原始的源相关信息
    */
   protected static class OriginAwareSystemEnvironmentPropertySource extends SystemEnvironmentPropertySource
         implements OriginLookup<String> {

      OriginAwareSystemEnvironmentPropertySource(String name, Map<String, Object> source) {
         super(name, source);
      }

      @Override
      public Origin getOrigin(String key) {
         String property = resolvePropertyName(key);
         if (super.containsProperty(property)) {
            return new SystemEnvironmentOrigin(property);
         }
         return null;
      }

   }

}
```

------

SpringApplicationJsonEnvironmentPostProcessor类也是EnvironmentPostProcessor环境后置处理器函数，其优先级比SystemEnvironmentPropertySourceEnvironmentPostProcessor高，会将系统属性spring.application.json或SPRING_APPLICATION_JSON解析成JSON,并存储到Environment：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201027112251129.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

上述是通过代码的 形式传递spring.application.json参数，也可以通过运行jar包时传递：

```java
java -jar demo-0.0.1-SNAPSHOT.jar --spring.application.json='{"a":"bar","b":23}'
 //或
java -Dspring.application.json='{"a":"bar","b":23}' -jar demo-0.0.1-SNAPSHOT.jar
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

