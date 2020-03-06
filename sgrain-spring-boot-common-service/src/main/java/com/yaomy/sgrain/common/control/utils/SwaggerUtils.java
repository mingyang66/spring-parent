package com.yaomy.sgrain.common.control.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: Swagger URI工具类
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@Deprecated
public class SwaggerUtils {
    public static final List<String> urls = Arrays.asList("/swagger-resources/configuration/ui",
                                                            "/swagger-resources/configuration/security",
                                                            "/swagger-resources",
                                                            "/v2/api-docs",
                                                            "/swagger-ui.html");

}
