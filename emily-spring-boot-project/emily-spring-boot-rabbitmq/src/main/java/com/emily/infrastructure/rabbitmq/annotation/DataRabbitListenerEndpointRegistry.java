package com.emily.infrastructure.rabbitmq.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.*;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DataRabbitListenerEndpointRegistry implements DisposableBean, SmartLifecycle, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final Map<String, MessageListenerContainer> listenerContainers = new ConcurrentHashMap();
    private final Lock listenerContainersLock = new ReentrantLock();
    private int phase = Integer.MAX_VALUE;
    private @Nullable ConfigurableApplicationContext applicationContext;
    private boolean contextRefreshed;

    public DataRabbitListenerEndpointRegistry() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext configurable) {
            this.applicationContext = configurable;
        }

    }

    public @Nullable MessageListenerContainer getListenerContainer(String id) {
        Assert.hasText(id, "Container identifier must not be empty");
        return (MessageListenerContainer) this.listenerContainers.get(id);
    }

    public Set<String> getListenerContainerIds() {
        return Collections.unmodifiableSet(this.listenerContainers.keySet());
    }

    public Collection<MessageListenerContainer> getListenerContainers() {
        return Collections.unmodifiableCollection(this.listenerContainers.values());
    }

    public void registerListenerContainer(RabbitListenerEndpoint endpoint, RabbitListenerContainerFactory<?> factory) {
        this.registerListenerContainer(endpoint, factory, false);
    }

    public void registerListenerContainer(RabbitListenerEndpoint endpoint, RabbitListenerContainerFactory<?> factory, boolean startImmediately) {
        Assert.notNull(endpoint, "Endpoint must not be null");
        Assert.notNull(factory, "Factory must not be null");
        String id = endpoint.getId();
        Assert.hasText(id, "Endpoint id must not be empty");
        this.listenerContainersLock.lock();

        try {
            Assert.state(!this.listenerContainers.containsKey(id), "Another endpoint is already registered with id '" + id + "'");
            MessageListenerContainer container = this.createListenerContainer(endpoint, factory);
            this.listenerContainers.put(id, container);
            if (StringUtils.hasText(endpoint.getGroup()) && this.applicationContext != null) {
                Object containerGroup;
                if (this.applicationContext.containsBean(endpoint.getGroup())) {
                    containerGroup = (List) this.applicationContext.getBean(endpoint.getGroup(), List.class);
                } else {
                    containerGroup = new ArrayList();
                    this.applicationContext.getBeanFactory().registerSingleton(endpoint.getGroup(), containerGroup);
                }

                ((List) containerGroup).add(container);
            }

            if (this.contextRefreshed) {
                container.lazyLoad();
            }

            if (startImmediately) {
                this.startIfNecessary(container);
            }
        } finally {
            this.listenerContainersLock.unlock();
        }

    }

    protected MessageListenerContainer createListenerContainer(RabbitListenerEndpoint endpoint, RabbitListenerContainerFactory<?> factory) {
        MessageListenerContainer listenerContainer = factory.createListenerContainer(endpoint);
        listenerContainer.afterPropertiesSet();
        int containerPhase = listenerContainer.getPhase();
        if (containerPhase < Integer.MAX_VALUE) {
            if (this.phase < Integer.MAX_VALUE && this.phase != containerPhase) {
                throw new IllegalStateException("Encountered phase mismatch between container factory definitions: " + this.phase + " vs " + containerPhase);
            }

            this.phase = listenerContainer.getPhase();
        }

        return listenerContainer;
    }

    public @Nullable MessageListenerContainer unregisterListenerContainer(String id) {
        return (MessageListenerContainer) this.listenerContainers.remove(id);
    }

    public void destroy() {
        Iterator var1 = this.getListenerContainers().iterator();

        while (var1.hasNext()) {
            MessageListenerContainer listenerContainer = (MessageListenerContainer) var1.next();
            if (listenerContainer instanceof DisposableBean disposable) {
                try {
                    disposable.destroy();
                } catch (Exception var5) {
                    Exception ex = var5;
                    this.logger.warn("Failed to destroy listener container [" + String.valueOf(listenerContainer) + "]", ex);
                }
            }
        }

    }

    public int getPhase() {
        return this.phase;
    }

    public boolean isAutoStartup() {
        return true;
    }

    public void start() {
        Iterator var1 = this.getListenerContainers().iterator();

        while (var1.hasNext()) {
            MessageListenerContainer listenerContainer = (MessageListenerContainer) var1.next();
            this.startIfNecessary(listenerContainer);
        }

    }

    public void stop() {
        Iterator var1 = this.getListenerContainers().iterator();

        while (var1.hasNext()) {
            MessageListenerContainer listenerContainer = (MessageListenerContainer) var1.next();
            listenerContainer.stop();
        }

    }

    public void stop(Runnable callback) {
        Collection<MessageListenerContainer> containers = this.getListenerContainers();
        if (!containers.isEmpty()) {
            AggregatingCallback aggregatingCallback = new AggregatingCallback(containers.size(), callback);
            Iterator var4 = containers.iterator();

            while (var4.hasNext()) {
                MessageListenerContainer listenerContainer = (MessageListenerContainer) var4.next();

                try {
                    listenerContainer.stop(aggregatingCallback);
                } catch (Exception var7) {
                    Exception e = var7;
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn("Failed to stop listener container [" + String.valueOf(listenerContainer) + "]", e);
                    }
                }
            }
        } else {
            callback.run();
        }

    }

    public boolean isRunning() {
        Iterator var1 = this.getListenerContainers().iterator();

        MessageListenerContainer listenerContainer;
        do {
            if (!var1.hasNext()) {
                return false;
            }

            listenerContainer = (MessageListenerContainer) var1.next();
        } while (!listenerContainer.isRunning());

        return true;
    }

    private void startIfNecessary(MessageListenerContainer listenerContainer) {
        if (this.contextRefreshed || listenerContainer.isAutoStartup()) {
            listenerContainer.start();
        }

    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().equals(this.applicationContext)) {
            this.contextRefreshed = true;
        }

    }

    private static final class AggregatingCallback implements Runnable {
        private final AtomicInteger count;
        private final Runnable finishCallback;

        AggregatingCallback(int count, Runnable finishCallback) {
            this.count = new AtomicInteger(count);
            this.finishCallback = finishCallback;
        }

        public void run() {
            if (this.count.decrementAndGet() == 0) {
                this.finishCallback.run();
            }

        }
    }
}

