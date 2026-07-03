package com.emily.infrastructure.amqp.autoconfigure;


import org.jspecify.annotations.Nullable;
import org.springframework.boot.amqp.autoconfigure.RabbitConnectionDetails;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * org.springframework.boot.amqp.autoconfigure.PropertiesRabbitConnectionDetails
 * 适配RabbitProperties到RabbitConnectionDetails
 */
public class DataPropertiesRabbitConnectionDetails implements RabbitConnectionDetails {
    private final RabbitProperties properties;
    private final @Nullable SslBundles sslBundles;

    DataPropertiesRabbitConnectionDetails(RabbitProperties properties, @Nullable SslBundles sslBundles) {
        this.properties = properties;
        this.sslBundles = sslBundles;
    }

    public String getUsername() {
        return this.properties.determineUsername();
    }

    public @Nullable String getPassword() {
        return this.properties.determinePassword();
    }

    public @Nullable String getVirtualHost() {
        return this.properties.determineVirtualHost();
    }

    @Override
    public List<Address> getAddresses() {
        List<Address> addresses = new ArrayList<>();
        for (String address : this.properties.determineAddresses()) {
            int portSeparatorIndex = address.lastIndexOf(':');
            String host = address.substring(0, portSeparatorIndex);
            String port = address.substring(portSeparatorIndex + 1);
            addresses.add(new Address(host, Integer.parseInt(port)));
        }
        return addresses;
    }

    @Override
    public @Nullable SslBundle getSslBundle() {
        RabbitProperties.Ssl ssl = this.properties.getSsl();
        if (!ssl.determineEnabled()) {
            return null;
        }
        if (StringUtils.hasLength(ssl.getBundle())) {
            Assert.notNull(this.sslBundles, "SSL bundle name has been set but no SSL bundles found in context");
            return this.sslBundles.getBundle(ssl.getBundle());
        }
        return null;
    }
}
