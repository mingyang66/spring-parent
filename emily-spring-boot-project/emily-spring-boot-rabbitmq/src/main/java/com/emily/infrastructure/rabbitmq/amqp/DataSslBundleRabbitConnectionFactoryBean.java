package com.emily.infrastructure.rabbitmq.amqp;

import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.boot.ssl.SslBundle;

/**
 * @author :  Emily
 * @since :  2025/12/4 下午5:39
 */
public class DataSslBundleRabbitConnectionFactoryBean extends RabbitConnectionFactoryBean {
    private @Nullable SslBundle sslBundle;
    private boolean enableHostnameVerification;

    DataSslBundleRabbitConnectionFactoryBean() {
    }

    protected void setUpSSL() {
        if (this.sslBundle != null) {
            this.connectionFactory.useSslProtocol(this.sslBundle.createSslContext());
            if (this.enableHostnameVerification) {
                this.connectionFactory.enableHostnameVerification();
            }
        } else {
            super.setUpSSL();
        }

    }

    void setSslBundle(@Nullable SslBundle sslBundle) {
        this.sslBundle = sslBundle;
    }

    public void setEnableHostnameVerification(boolean enable) {
        this.enableHostnameVerification = enable;
        super.setEnableHostnameVerification(enable);
    }
}
