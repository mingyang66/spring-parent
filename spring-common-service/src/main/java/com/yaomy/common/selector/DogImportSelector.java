package com.yaomy.common.selector;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.selector.DogImportSelector
 * @Date: 2019/8/6 10:55
 * @Version: 1.0
 */
public class DogImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Set<String> methodMetadata = importingClassMetadata.getAnnotationTypes();
        for (String meta: methodMetadata
             ) {
           // System.out.println(meta+"---------------");
        }
        return new String[]{BlackDog.class.getName(), YellowDog.class.getName()};
    }
}
