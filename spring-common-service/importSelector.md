### Spring之ImportSelector接口浅析

@Import注解是将指定的Bean加入到IOC容器之中进行管理，ImportSelector接口只有一个selectImports方法，该方法将返回一个数组，也就是类实例名称，@Import
注解将会把返回的Bean加入到IOC容器中进行管理；

#### 1.看下ImportSelector接口的源码

```
/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that determine which @{@link Configuration}
 * class(es) should be imported based on a given selection criteria, usually one or
 * more annotation attributes.
 *
 * <p>An {@link ImportSelector} may implement any of the following
 * {@link org.springframework.beans.factory.Aware Aware} interfaces,
 * and their respective methods will be called prior to {@link #selectImports}:
 * <ul>
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}</li>
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}</li>
 * </ul>
 *
 * <p>{@code ImportSelector} implementations are usually processed in the same way
 * as regular {@code @Import} annotations, however, it is also possible to defer
 * selection of imports until all {@code @Configuration} classes have been processed
 * (see {@link DeferredImportSelector} for details).
 *
 * @author Chris Beams
 * @since 3.1
 * @see DeferredImportSelector
 * @see Import
 * @see ImportBeanDefinitionRegistrar
 * @see Configuration
 */
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
