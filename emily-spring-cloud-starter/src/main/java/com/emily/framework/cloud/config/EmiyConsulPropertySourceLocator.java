package com.emily.framework.cloud.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.consul.config.ConsulConfigProperties;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.cloud.consul.config.ConsulConfigProperties.Format.FILES;

/**
 * @author Spencer Gibb
 */
@Order(-1)
public class EmiyConsulPropertySourceLocator implements PropertySourceLocator {

    private static final Log log = LogFactory.getLog(org.springframework.cloud.consul.config.ConsulPropertySourceLocator.class);

    private final ConsulClient consul;

    private final ConsulConfigProperties properties;

    private final List<String> contexts = new ArrayList<>();

    private final LinkedHashMap<String, Long> contextIndex = new LinkedHashMap<>();

    public EmiyConsulPropertySourceLocator(ConsulClient consul,
                                           ConsulConfigProperties properties) {
        this.consul = consul;
        this.properties = properties;
    }

    @Deprecated
    public List<String> getContexts() {
        return this.contexts;
    }

    public LinkedHashMap<String, Long> getContextIndexes() {
        return this.contextIndex;
    }

    @Override
    @Retryable(interceptor = "consulRetryInterceptor")
    public Collection<PropertySource<?>> locateCollection(Environment environment) {
        return PropertySourceLocator.locateCollection(this, environment);
    }

    @Override
    @Retryable(interceptor = "consulRetryInterceptor")
    public PropertySource<?> locate(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment env = (ConfigurableEnvironment) environment;

            String appName = this.properties.getName();

            if (appName == null) {
                appName = env.getProperty("spring.application.name");
            }

            List<String> profiles = Arrays.asList(env.getActiveProfiles());

            String prefix = this.properties.getPrefix();

            List<String> suffixes = new ArrayList<>();
            if (this.properties.getFormat() != FILES) {
                suffixes.add("/");
            }
            else {
                suffixes.add(".yml");
                suffixes.add(".yaml");
                suffixes.add(".properties");
            }

            String defaultContext = getContext(prefix,
                    this.properties.getDefaultContext());

            for (String suffix : suffixes) {
                this.contexts.add(defaultContext + suffix);
            }
            for (String suffix : suffixes) {
                addProfiles(this.contexts, defaultContext, profiles, suffix);
            }

            String baseContext = getContext(prefix, appName);

            for (String suffix : suffixes) {
                this.contexts.add(baseContext + suffix);
            }
            for (String suffix : suffixes) {
                addProfiles(this.contexts, baseContext, profiles, suffix);
            }

            Collections.reverse(this.contexts);

            CompositePropertySource composite = new CompositePropertySource("consul");

            for (String propertySourceContext : this.contexts) {
                try {
                    EmiyConsulPropertySource propertySource = null;
                    if (this.properties.getFormat() == FILES) {
                        Response<GetValue> response = this.consul.getKVValue(
                                propertySourceContext, this.properties.getAclToken());
                        addIndex(propertySourceContext, response.getConsulIndex());
                        if (response.getValue() != null) {
                            EmiyConsulFilesPropertySource filesPropertySource = new EmiyConsulFilesPropertySource(
                                    propertySourceContext, this.consul, this.properties);
                            filesPropertySource.init(response.getValue());
                            propertySource = filesPropertySource;
                        }
                    }
                    else {
                        propertySource = create(propertySourceContext, this.contextIndex);
                    }
                    if (propertySource != null) {
                        composite.addPropertySource(propertySource);
                    }
                }
                catch (Exception e) {
                    if (this.properties.isFailFast()) {
                        log.error(
                                "Fail fast is set and there was an error reading configuration from consul.");
                        ReflectionUtils.rethrowRuntimeException(e);
                    }
                    else {
                        log.warn("Unable to load consul config from "
                                + propertySourceContext, e);
                    }
                }
            }

            return composite;
        }
        return null;
    }

    private String getContext(String prefix, String context) {
        if (StringUtils.isEmpty(prefix)) {
            return context;
        }
        else {
            return prefix + "/" + context;
        }
    }

    private void addIndex(String propertySourceContext, Long consulIndex) {
        this.contextIndex.put(propertySourceContext, consulIndex);
    }

    private EmiyConsulPropertySource create(String context, Map<String, Long> contextIndex) {
        EmiyConsulPropertySource propertySource = new EmiyConsulPropertySource(context,
                this.consul, this.properties);
        propertySource.init();
        addIndex(context, propertySource.getInitialIndex());
        return propertySource;
    }

    private void addProfiles(List<String> contexts, String baseContext,
                             List<String> profiles, String suffix) {
        for (String profile : profiles) {
            contexts.add(baseContext + this.properties.getProfileSeparator() + profile
                    + suffix);
        }
    }

}
