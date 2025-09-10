package com.emily.sample.email.config;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Properties;

/**
 * {@link org.springframework.boot.autoconfigure.mail.MailSenderPropertiesConfiguration}
 *
 * @author :  Emily
 * @since :  2025/8/28 上午10:51
 */
public class PooledMailConnectionFactory extends BasePooledObjectFactory<JavaMailSender> {
    private final MailProperties properties;
    private final ObjectProvider<SslBundles> sslBundles;

    public PooledMailConnectionFactory(MailProperties properties, ObjectProvider<SslBundles> sslBundles) {
        this.properties = properties;
        this.sslBundles = sslBundles;
    }

    @Override
    public JavaMailSender create() throws Exception {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(properties, sender, sslBundles.getIfAvailable());
        return sender;
    }

    @Override
    public PooledObject<JavaMailSender> wrap(JavaMailSender javaMailSender) {
        return new DefaultPooledObject<>(javaMailSender);
    }

    private void applyProperties(MailProperties properties, JavaMailSenderImpl sender, SslBundles sslBundles) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }
        Properties javaMailProperties = asProperties(properties.getProperties());
        String protocol = properties.getProtocol();
        protocol = (!StringUtils.hasLength(protocol)) ? "smtp" : protocol;
        MailProperties.Ssl ssl = properties.getSsl();
        if (ssl.isEnabled()) {
            javaMailProperties.setProperty("mail." + protocol + ".ssl.enable", "true");
        }
        if (ssl.getBundle() != null) {
            SslBundle sslBundle = sslBundles.getBundle(ssl.getBundle());
            javaMailProperties.put("mail." + protocol + ".ssl.socketFactory",
                    sslBundle.createSslContext().getSocketFactory());
        }
        if (!javaMailProperties.isEmpty()) {
            sender.setJavaMailProperties(javaMailProperties);
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }
}

