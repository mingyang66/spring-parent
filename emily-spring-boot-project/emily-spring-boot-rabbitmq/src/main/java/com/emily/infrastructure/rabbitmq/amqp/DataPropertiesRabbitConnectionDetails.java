package com.emily.infrastructure.rabbitmq.amqp;


import org.jspecify.annotations.Nullable;
import org.springframework.boot.amqp.autoconfigure.RabbitConnectionDetails;
import org.springframework.boot.amqp.autoconfigure.RabbitProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataPropertiesRabbitConnectionDetails  implements RabbitConnectionDetails {
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

    public List<Address> getAddresses() {
        List<RabbitConnectionDetails.Address> addresses = new ArrayList();
        Iterator var2 = this.properties.determineAddresses().iterator();

        while(var2.hasNext()) {
            String address = (String)var2.next();
            int portSeparatorIndex = address.lastIndexOf(58);
            String host = address.substring(0, portSeparatorIndex);
            String port = address.substring(portSeparatorIndex + 1);
            addresses.add(new RabbitConnectionDetails.Address(host, Integer.parseInt(port)));
        }

        return addresses;
    }

    public @Nullable SslBundle getSslBundle() {
        RabbitProperties.Ssl ssl = this.properties.getSsl();
        if (!ssl.determineEnabled()) {
            return null;
        } else if (StringUtils.hasLength(ssl.getBundle())) {
            Assert.notNull(this.sslBundles, "SSL bundle name has been set but no SSL bundles found in context");
            return this.sslBundles.getBundle(ssl.getBundle());
        } else {
            return null;
        }
    }
}
