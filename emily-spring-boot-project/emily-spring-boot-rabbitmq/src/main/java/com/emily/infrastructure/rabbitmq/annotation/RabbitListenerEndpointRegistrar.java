package com.emily.infrastructure.rabbitmq.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;

public class RabbitListenerEndpointRegistrar implements BeanFactoryAware, InitializingBean {
    private final List<AmqpListenerEndpointDescriptor> endpointDescriptors = new ArrayList();
    private final Lock endpointDescriptorsLock = new ReentrantLock();
    private List<HandlerMethodArgumentResolver> customMethodArgumentResolvers = new ArrayList();
    private @Nullable RabbitListenerEndpointRegistry endpointRegistry;
    private @Nullable MessageHandlerMethodFactory messageHandlerMethodFactory;
    private @Nullable RabbitListenerContainerFactory<?> containerFactory;
    private @Nullable String containerFactoryBeanName;
    private BeanFactory beanFactory;
    private boolean startImmediately;
    private @Nullable Validator validator;

    public RabbitListenerEndpointRegistrar() {
    }

    public void setEndpointRegistry(RabbitListenerEndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    public @Nullable RabbitListenerEndpointRegistry getEndpointRegistry() {
        return this.endpointRegistry;
    }

    public List<HandlerMethodArgumentResolver> getCustomMethodArgumentResolvers() {
        return Collections.unmodifiableList(this.customMethodArgumentResolvers);
    }

    public void setCustomMethodArgumentResolvers(HandlerMethodArgumentResolver... methodArgumentResolvers) {
        this.customMethodArgumentResolvers = Arrays.asList(methodArgumentResolvers);
    }

    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory rabbitHandlerMethodFactory) {
        Assert.isNull(this.validator, "A validator cannot be provided with a custom message handler factory");
        this.messageHandlerMethodFactory = rabbitHandlerMethodFactory;
    }

    public @Nullable MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        return this.messageHandlerMethodFactory;
    }

    public void setContainerFactory(RabbitListenerContainerFactory<?> containerFactory) {
        this.containerFactory = containerFactory;
    }

    public void setContainerFactoryBeanName(String containerFactoryBeanName) {
        this.containerFactoryBeanName = containerFactoryBeanName;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public @Nullable Validator getValidator() {
        return this.validator;
    }

    public void setValidator(Validator validator) {
        Assert.isNull(this.messageHandlerMethodFactory, "A validator cannot be provided with a custom message handler factory");
        this.validator = validator;
    }

    public void afterPropertiesSet() {
        this.registerAllEndpoints();
    }

    protected void registerAllEndpoints() {
        Assert.state(this.endpointRegistry != null, "No registry available");
        this.endpointDescriptorsLock.lock();

        try {
            AmqpListenerEndpointDescriptor descriptor;
            for(Iterator var1 = this.endpointDescriptors.iterator(); var1.hasNext(); this.endpointRegistry.registerListenerContainer(descriptor.endpoint, this.resolveContainerFactory(descriptor))) {
                descriptor = (AmqpListenerEndpointDescriptor)var1.next();
                RabbitListenerEndpoint var4 = descriptor.endpoint;
                if (var4 instanceof MultiMethodRabbitListenerEndpoint multi) {
                    if (this.validator != null) {
                        multi.setValidator(this.validator);
                    }
                }
            }

            this.startImmediately = true;
        } finally {
            this.endpointDescriptorsLock.unlock();
        }
    }

    private RabbitListenerContainerFactory<?> resolveContainerFactory(AmqpListenerEndpointDescriptor descriptor) {
        if (descriptor.containerFactory != null) {
            return descriptor.containerFactory;
        } else if (this.containerFactory != null) {
            return this.containerFactory;
        } else if (this.containerFactoryBeanName != null) {
            this.containerFactory = (RabbitListenerContainerFactory)this.beanFactory.getBean(this.containerFactoryBeanName, RabbitListenerContainerFactory.class);
            return this.containerFactory;
        } else {
            String var10002 = RabbitListenerContainerFactory.class.getSimpleName();
            throw new IllegalStateException("Could not resolve the " + var10002 + " to use for [" + String.valueOf(descriptor.endpoint) + "] no factory was given and no default is set.");
        }
    }

    public void registerEndpoint(RabbitListenerEndpoint endpoint, @Nullable RabbitListenerContainerFactory<?> factory) {
        Assert.notNull(endpoint, "Endpoint must be set");
        Assert.hasText(endpoint.getId(), "Endpoint id must be set");
        Assert.state(!this.startImmediately || this.endpointRegistry != null, "No registry available");
        AmqpListenerEndpointDescriptor descriptor = new AmqpListenerEndpointDescriptor(endpoint, factory);
        this.endpointDescriptorsLock.lock();

        try {
            if (this.startImmediately) {
                this.endpointRegistry.registerListenerContainer(descriptor.endpoint, this.resolveContainerFactory(descriptor), true);
            } else {
                this.endpointDescriptors.add(descriptor);
            }
        } finally {
            this.endpointDescriptorsLock.unlock();
        }

    }

    public void registerEndpoint(RabbitListenerEndpoint endpoint) {
        this.registerEndpoint(endpoint, (RabbitListenerContainerFactory)null);
    }

    private static record AmqpListenerEndpointDescriptor(RabbitListenerEndpoint endpoint, @Nullable RabbitListenerContainerFactory<?> containerFactory) {
        private AmqpListenerEndpointDescriptor(RabbitListenerEndpoint endpoint, @Nullable RabbitListenerContainerFactory<?> containerFactory) {
            this.endpoint = endpoint;
            this.containerFactory = containerFactory;
        }

        public RabbitListenerEndpoint endpoint() {
            return this.endpoint;
        }

        public @Nullable RabbitListenerContainerFactory<?> containerFactory() {
            return this.containerFactory;
        }
    }
}
