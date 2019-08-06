### Spring之ImportSelector接口浅析

@Import注解是将指定的Bean加入到IOC容器之中进行管理，ImportSelector接口只有一个selectImports方法，该方法将返回一个数组，也就是类实例名称，@Import
注解将会把返回的Bean加入到IOC容器中进行管理；

#### 1.看下ImportSelector接口的源码

```
package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;

public interface ImportSelector {

	/**
	 * Select and return the names of which class(es) should be imported based on
	 * the {@link AnnotationMetadata} of the importing @{@link Configuration} class.
	 */
	String[] selectImports(AnnotationMetadata importingClassMetadata);

}
```

selectImports方法返回String[],需要加入IOC容器进行管理的类都可以添加到数组之中，参数是AnnotationMetadata类型的，实现了ClassMetadata, AnnotatedTypeMetadata
类，所以可以对@Import注解引入ImportSelector实现类所在的类上的注解及类实例本身进行操作，可以在selectImports方法中进行各种逻辑上的处理；

#### 2.新增一个接口Dog
```
public interface Dog {
    String dogName();
}
```
#### 3.新增实现类BlackDog
```
public class BlackDog implements Dog {
    @Override
    public String dogName() {
        return "黑色Dog";
    }
}
```
#### 4.新增实现类YellowDog
```
public class YellowDog implements Dog {
    @Override
    public String dogName() {
        return "黄色Dog";
    }
}
```
#### 5.新增ApplicationContextUtil类打印出容器中的bean
```
@Component
@Import(DogImportSelector.class)
public class ApplicationContextUtil implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        String[] names = applicationContext.getBeanDefinitionNames() ;
        for (String name:names
             ) {
            System.out.println(name);
        }
    }
}
```
启动项目打印结果如下：
```
applicationContextUtil
com.yaomy.common.selector.BlackDog
com.yaomy.common.selector.YellowDog
```
