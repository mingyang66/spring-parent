package com.emily.framework.cloud.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.springframework.cloud.consul.config.ConsulConfigProperties;

import static org.springframework.cloud.consul.config.ConsulConfigProperties.Format.PROPERTIES;
import static org.springframework.cloud.consul.config.ConsulConfigProperties.Format.YAML;

/**
 * @author Spencer Gibb
 */
public class EmiyConsulFilesPropertySource extends EmiyConsulPropertySource {

    public EmiyConsulFilesPropertySource(String context, ConsulClient source,
                                         ConsulConfigProperties configProperties) {
        super(context, source, configProperties);
    }

    @Override
    public void init() {
        // noop
    }

    public void init(GetValue value) {
        if (this.getContext().endsWith(".yml") || this.getContext().endsWith(".yaml")) {
            parseValue(value, YAML);
        }
        else if (this.getContext().endsWith(".properties")) {
            parseValue(value, PROPERTIES);
        }
        else {
            throw new IllegalStateException(
                    "Unknown files extension for context " + this.getContext());
        }
    }

}
