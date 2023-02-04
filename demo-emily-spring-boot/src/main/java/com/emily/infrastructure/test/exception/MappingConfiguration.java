package com.emily.infrastructure.test.exception;

import com.emily.infrastructure.autoconfigure.route.LookupPathAutoConfiguration;
import com.emily.infrastructure.autoconfigure.route.mapping.LookupPathCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 2:10 下午
 */
@AutoConfiguration(before = LookupPathAutoConfiguration.class)
public class MappingConfiguration {

    @Bean
    public LookupPathCustomizer lookupPathCustomizer(){
        return new LookupPathCustomizer() {
            @Override
            public String resolveSpecifiedLookupPath(String lookupPath) {
                return lookupPath;
            }
        };
    }
}
