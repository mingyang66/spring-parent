package com.emily.infrastructure.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @Description Swagger配置
 * @ProjectName com.eastmoney.emis.simpletemplate
 * @Package
 * @Author 韩庆瑞
 * @Date 2019/08/12 16:00
 * @Version 1.0
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    @Bean
    Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(createApiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.eastmoney.emis.boot.demo.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo createApiInfo() {
        return new ApiInfoBuilder()
                .description("交易中台账户模块")
                .contact(new Contact("韩庆瑞", "http://172.30.64.81/emis/api/com.eastmoney.emis.trade.api.git", "qingrui@eastmoney.com"))
                .version("v1.0.0")
                .title("API文档")
                .license("Apache2.0")
                .licenseUrl("http://www,apache.org/licenses/LICENSE-2.0")
                .build();
    }
}
