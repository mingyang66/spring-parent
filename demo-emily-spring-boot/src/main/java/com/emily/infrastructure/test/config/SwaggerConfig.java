package com.emily.infrastructure.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


/**
 * @author Emily
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .apiInfo(new ApiInfoBuilder()
                        .title("API文档")
                        .description("交易中台账户模块")
                        .termsOfServiceUrl("https://github.com/mingyang66/spring-parent")
                        .contact(new Contact("Emily", "https://github.com/mingyang66/spring-parent", "mingyangsky@foxmail.com"))
                        .version("v1.0.0")
                        .license("Apache2.0")
                        .licenseUrl("http://www,apache.org/licenses/LICENSE-2.0")
                        .build())
                .groupName("账户服务")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emily.infrastructure.test.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public Docket docketPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .apiInfo(new ApiInfoBuilder()
                        .title("Plugin API文档")
                        .description("交易中台账户模块")
                        .termsOfServiceUrl("https://github.com/mingyang66/spring-parent")
                        .contact(new Contact("Emily", "https://github.com/mingyang66/spring-parent", "mingyangsky@foxmail.com"))
                        .version("v1.0.0")
                        .license("Apache2.0")
                        .licenseUrl("http://www,apache.org/licenses/LICENSE-2.0")
                        .build())
                .groupName("插件Plugin")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emily.infrastructure.test.plugin.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
