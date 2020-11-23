package com.sgrain.boot.swagger.config;

import com.sgrain.boot.common.utils.log.LoggerUtils;
import com.sgrain.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import javax.annotation.PostConstruct;

/**
 * @program: spring-parent
 * @description: swagger配置类
 * @create: 2020/07/06
 */
@EnableSwagger2WebMvc
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "spring.sgrain.swagger", name = "enable", havingValue = "true", matchIfMissing = true)
public class SwaggerAutoConfiguration implements CommandLineRunner {
    @Autowired
    private DefaultListableBeanFactory defaultListableBeanFactory;
    @Autowired
    private SwaggerProperties swaggerProperties;

    @PostConstruct
    public void register() {
        if(!swaggerProperties.isEnable()){
            return;
        }
        //获取分组
        String[] groups = StringUtils.split(swaggerProperties.getGroup(), CharacterUtils.COMMA_EN);
        String[] groupNames = StringUtils.split(swaggerProperties.getGroupName(), CharacterUtils.COMMA_EN);
        String[] basePackages = StringUtils.split(swaggerProperties.getBasePackage(), CharacterUtils.COMMA_EN);
        if (ArrayUtils.isEmpty(groups)) {
            groups = new String[]{"default"};
        }
        if (ArrayUtils.isEmpty(groupNames)) {
            groupNames = new String[]{"小米粒"};
        }
        if (ArrayUtils.isEmpty(basePackages)) {
            basePackages = new String[]{"com.sgrain.boot"};
        }

        for (int i = 0; i < groups.length; i++) {
            if (i > groupNames.length - 1) {
                continue;
            }
            if (i > basePackages.length - 1) {
                continue;
            }
            String group = StringUtils.join(groups[i], CharacterUtils.LINE_THROUGH_CENTER, "Definition");
            defaultListableBeanFactory.registerSingleton(group, new Docket(DocumentationType.SWAGGER_2)
                    .groupName(groupNames[i])
                    .apiInfo(apiInfo())
                    .enable(swaggerProperties.isEnable())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage(basePackages[i]))
                    .paths(PathSelectors.any())
                    .build()
                    .pathMapping("/"));
        }
    }

    /**
     * 定义API文档描述信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getApiInfo().getTitle())
                .description(swaggerProperties.getApiInfo().getDescription())
                //.termsOfServiceUrl("服务条款URL")
                //.contact(new Contact("小米粒（sgrain）", "http://www.sgrain.com", "sgrain@qq.com"))
                //.license("Apache2.0")
                //.licenseUrl("http://www,apache.org/licenses/LICENSE-2.0")
                .version(swaggerProperties.getApiInfo().getVersion())
                .build();
    }

    @Override
    public void run(String... args) throws Exception {
        LoggerUtils.info(SwaggerAutoConfiguration.class, "【自动化配置】----Swagger组件初始化完成...");
    }
}
