package com.emily.infrastructure.logback.util;

import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

/**
 * @program: spring-parent
 * @description: cloud微服务判定
 * @author: Emily
 * @create: 2022/02/08
 * @since 4.0.7
 */
public abstract class PropertyUtils {
    public static final String BOOTSTRAP_ENABLED_PROPERTY = "spring.cloud.bootstrap.enabled";
    public static final String USE_LEGACY_PROCESSING_PROPERTY = "spring.config.use-legacy-processing";
    public static final String MARKER_CLASS = "org.springframework.cloud.bootstrap.marker.Marker";
    public static final boolean MARKER_CLASS_EXISTS = ClassUtils.isPresent("org.springframework.cloud.bootstrap.marker.Marker", (ClassLoader)null);

    private PropertyUtils() {
        throw new UnsupportedOperationException("unable to instatiate utils class");
    }

    public static boolean bootstrapEnabled(Environment environment) {
        return environment.getProperty("spring.cloud.bootstrap.enabled", Boolean.class, false) || MARKER_CLASS_EXISTS;
    }

    public static boolean useLegacyProcessing(Environment environment) {
        return environment.getProperty("spring.config.use-legacy-processing", Boolean.class, false);
    }
}
