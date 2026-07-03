package com.emily.infrastructure.amqp.rabbit.annotation;

import com.emily.infrastructure.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import com.emily.infrastructure.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.core.*;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.listener.AbstractListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.listener.adapter.AmqpMessageHandlerMethodFactory;
import org.springframework.amqp.listener.adapter.ReplyPostProcessor;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.BytesToStringConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 重构时注意{@link RabbitListenerAnnotationBeanPostProcessor#resolveContainerFactory(RabbitListener, Object, String)}方法会提前触发初始化容器
 */
public class RabbitListenerAnnotationBeanPostProcessor extends AbstractListenerAnnotationBeanPostProcessor<RabbitListener> {
    public static final String DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME = "rabbitListenerContainerFactory";
    public static final String RABBIT_EMPTY_STRING_ARGUMENTS_PROPERTY = "spring.rabbitmq.emptyStringArguments";
    private final Set<String> emptyStringArguments = new HashSet();
    private @Nullable RabbitListenerEndpointRegistry endpointRegistry;
    private String defaultContainerFactoryBeanName = "rabbitListenerContainerFactory";
    private final RabbitHandlerMethodFactoryAdapter messageHandlerMethodFactory = new RabbitHandlerMethodFactoryAdapter();
    private final RabbitListenerEndpointRegistrar registrar = new RabbitListenerEndpointRegistrar();
    private final AtomicInteger counter = new AtomicInteger();
    private int increment;
    private Charset charset;

    public RabbitListenerAnnotationBeanPostProcessor() {
        this.charset = StandardCharsets.UTF_8;
        this.emptyStringArguments.add("x-dead-letter-exchange");
    }

    public void setEndpointRegistry(RabbitListenerEndpointRegistry endpointRegistry) {
        this.endpointRegistry = endpointRegistry;
    }

    public void setContainerFactoryBeanName(String containerFactoryBeanName) {
        this.defaultContainerFactoryBeanName = containerFactoryBeanName;
    }

    public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory messageHandlerMethodFactory) {
        this.messageHandlerMethodFactory.setMessageHandlerMethodFactory(messageHandlerMethodFactory);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        return this.messageHandlerMethodFactory;
    }

    public void setEnvironment(Environment environment) {
        super.setEnvironment(environment);
        String property = (String) environment.getProperty("spring.rabbitmq.emptyStringArguments", String.class);
        if (property != null) {
            this.emptyStringArguments.addAll(StringUtils.commaDelimitedListToSet(property));
        }

    }

    public void afterSingletonsInstantiated() {
        super.afterSingletonsInstantiated();
        this.registrar.setBeanFactory(this.getBeanFactory());
        BeanFactory var2 = this.getBeanFactory();
        if (var2 instanceof ListableBeanFactory lbf) {
            Map<String, RabbitListenerConfigurer> instances = lbf.getBeansOfType(RabbitListenerConfigurer.class, false, false);

            for (RabbitListenerConfigurer configurer : instances.values()) {
                configurer.configureRabbitListeners(this.registrar);
            }
        }

        if (this.registrar.getEndpointRegistry() == null) {
            if (this.endpointRegistry == null) {
                this.endpointRegistry = (RabbitListenerEndpointRegistry) this.getBeanFactory().getBean("org.springframework.amqp.rabbit.config.internalRabbitListenerEndpointRegistry", RabbitListenerEndpointRegistry.class);
            }

            this.registrar.setEndpointRegistry(this.endpointRegistry);
        }

        this.registrar.setContainerFactoryBeanName(this.defaultContainerFactoryBeanName);
        MessageHandlerMethodFactory handlerMethodFactory = this.registrar.getMessageHandlerMethodFactory();
        if (handlerMethodFactory != null) {
            this.messageHandlerMethodFactory.setMessageHandlerMethodFactory(handlerMethodFactory);
        }

        this.registrar.afterPropertiesSet();
    }

    protected AbstractListenerAnnotationBeanPostProcessor.TypeMetadata<RabbitListener> buildMetadata(Class<?> clazz) {
        return buildMetadata(clazz, RabbitListener.class, RabbitHandler.class);
    }

    protected void doProcessAmqpListener(RabbitListener listenerAnnotation, Method method, Object bean, String beanName) {
        this.processAmqpListener(listenerAnnotation, method, bean, beanName);
    }

    protected Collection<Declarable> processAmqpListener(RabbitListener rabbitListener, Method method, Object bean, String beanName) {
        Method methodToUse = checkProxy(method, bean);
        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
        endpoint.setMethod(methodToUse);
        return this.processListener(endpoint, rabbitListener, bean, methodToUse, beanName);
    }

    protected void processMultiMethodListeners(List<RabbitListener> classLevelListeners, List<Method> multiMethods, Object bean, String beanName) {
        List<Method> checkedMethods = new ArrayList(multiMethods.size());
        Method defaultMethod = null;

        for (Method method : multiMethods) {
            Method checked = checkProxy(method, bean);
            RabbitHandler annotation = (RabbitHandler) AnnotationUtils.findAnnotation(method, RabbitHandler.class);
            if (annotation != null && annotation.isDefault()) {
                Method finalDefaultMethod = defaultMethod;
                Assert.state(defaultMethod == null, () -> {
                    String var10000 = String.valueOf(finalDefaultMethod);
                    return "Only one @RabbitHandler can be marked 'isDefault', found: " + var10000 + " and " + String.valueOf(method);
                });
                defaultMethod = checked;
            }

            checkedMethods.add(checked);
        }

        for (RabbitListener classLevelListener : classLevelListeners) {
            MultiMethodRabbitListenerEndpoint endpoint = new MultiMethodRabbitListenerEndpoint(checkedMethods, defaultMethod, bean);
            this.processListener(endpoint, classLevelListener, bean, bean.getClass(), beanName);
        }

    }

    protected Collection<Declarable> processListener(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object bean, Object target, String beanName) {
        List<Declarable> declarables = new ArrayList();
        endpoint.setBean(bean);
        endpoint.setMessageHandlerMethodFactory(this.messageHandlerMethodFactory);
        String endpointId = this.getEndpointId(rabbitListener);
        if (StringUtils.hasText(endpointId)) {
            endpoint.setId(endpointId);
        }

        List<Object> resolvedQueues = this.resolveQueues(rabbitListener, declarables);
        if (!resolvedQueues.isEmpty()) {
            if (resolvedQueues.get(0) instanceof String) {
                endpoint.setQueueNames((String[]) resolvedQueues.stream().map((o) -> (String) o).toArray((x$0) -> new String[x$0]));
            } else {
                endpoint.setQueues((Queue[]) resolvedQueues.stream().map((o) -> (Queue) o).toArray((x$0) -> new Queue[x$0]));
            }
        }

        endpoint.setConcurrency(this.resolveExpressionAsStringOrInteger(rabbitListener.concurrency(), "concurrency"));
        endpoint.setBeanFactory(this.getBeanFactory());
        endpoint.setReturnExceptions(this.resolveExpressionAsBoolean(rabbitListener.returnExceptions()));
        this.resolveErrorHandler(endpoint, rabbitListener);
        String group = rabbitListener.group();
        if (StringUtils.hasText(group)) {
            Object resolvedGroup = this.resolveExpression(group);
            if (resolvedGroup instanceof String) {
                String str = (String) resolvedGroup;
                endpoint.setGroup(str);
            }
        }

        String autoStartup = rabbitListener.autoStartup();
        if (StringUtils.hasText(autoStartup)) {
            endpoint.setAutoStartup(this.resolveExpressionAsBoolean(autoStartup));
        }

        endpoint.setExclusive(rabbitListener.exclusive());
        String priority = this.resolveExpressionAsString(rabbitListener.priority(), "priority");
        if (StringUtils.hasText(priority)) {
            try {
                endpoint.setPriority(Integer.valueOf(priority));
            } catch (NumberFormatException ex) {
                throw new BeanInitializationException("Invalid priority value for " + String.valueOf(rabbitListener) + " (must be an integer)", ex);
            }
        }

        this.resolveExecutor(endpoint, rabbitListener, target, beanName);
        this.resolveAdmin(endpoint, rabbitListener, target);
        this.resolveAckMode(endpoint, rabbitListener);
        this.resolvePostProcessor(endpoint, rabbitListener, target, beanName);
        this.resolveMessageConverter(endpoint, rabbitListener, target, beanName);
        this.resolveReplyContentType(endpoint, rabbitListener);
        if (StringUtils.hasText(rabbitListener.batch())) {
            endpoint.setBatchListener(Boolean.parseBoolean(rabbitListener.batch()));
        }

        RabbitListenerContainerFactory<?> factory = this.resolveContainerFactory(rabbitListener, target, beanName);
        this.registrar.registerEndpoint(endpoint, factory);
        return declarables;
    }

    private void resolveErrorHandler(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener) {
        Object errorHandler = this.resolveExpression(rabbitListener.errorHandler());
        if (errorHandler instanceof RabbitListenerErrorHandler rleh) {
            endpoint.setErrorHandler(rleh);
        } else {
            String errorHandlerBeanName = this.resolveExpressionAsString(rabbitListener.errorHandler(), "errorHandler");
            if (StringUtils.hasText(errorHandlerBeanName)) {
                endpoint.setErrorHandler((RabbitListenerErrorHandler) this.getBeanFactory().getBean(errorHandlerBeanName, RabbitListenerErrorHandler.class));
            }
        }

    }

    private void resolveAckMode(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener) {
        String ackModeAttr = rabbitListener.ackMode();
        if (StringUtils.hasText(ackModeAttr)) {
            Object ackMode = this.resolveExpression(ackModeAttr);
            if (ackMode instanceof String) {
                String str = (String) ackMode;
                endpoint.setAckMode(AcknowledgeMode.valueOf(str));
            } else if (ackMode instanceof AcknowledgeMode) {
                AcknowledgeMode mode = (AcknowledgeMode) ackMode;
                endpoint.setAckMode(mode);
            } else {
                Assert.isNull(ackMode, "ackMode must resolve to a String or AcknowledgeMode");
            }
        }

    }

    private void resolveAdmin(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object adminTarget) {
        Object resolved = this.resolveExpression(rabbitListener.admin());
        if (resolved instanceof AmqpAdmin admin) {
            endpoint.setAdmin(admin);
        } else {
            String rabbitAdmin = this.resolveExpressionAsString(rabbitListener.admin(), "admin");
            if (StringUtils.hasText(rabbitAdmin)) {
                try {
                    endpoint.setAdmin((AmqpAdmin) this.getBeanFactory().getBean(rabbitAdmin, RabbitAdmin.class));
                } catch (NoSuchBeanDefinitionException ex) {
                    throw new BeanInitializationException("Could not register rabbit listener endpoint on [" + String.valueOf(adminTarget) + "], no " + RabbitAdmin.class.getSimpleName() + " with id '" + rabbitAdmin + "' was found in the application context", ex);
                }
            }
        }

    }

    private @Nullable RabbitListenerContainerFactory<?> resolveContainerFactory(RabbitListener rabbitListener, Object factoryTarget, String beanName) {
        RabbitListenerContainerFactory<?> factory = null;
        Object resolved = this.resolveExpression(rabbitListener.containerFactory());
        if (resolved instanceof RabbitListenerContainerFactory<?> rlcf) {
            return rlcf;
        } else {
            String containerFactoryBeanName = this.resolveExpressionAsString(rabbitListener.containerFactory(), "containerFactory");
            if (StringUtils.hasText(containerFactoryBeanName)) {
                try {
                    if (!this.getBeanFactory().containsBean(containerFactoryBeanName) && this.getBeanFactory() instanceof DefaultListableBeanFactory defaultListableBeanFactory) {
                        defaultListableBeanFactory.getBeansOfType(RabbitListenerContainerFactory.class, false, true);
                    }
                    factory = this.getBeanFactory().getBean(containerFactoryBeanName, RabbitListenerContainerFactory.class);
                } catch (NoSuchBeanDefinitionException ex) {
                    throw new BeanInitializationException(noBeanFoundMessage(factoryTarget, beanName, containerFactoryBeanName, RabbitListenerContainerFactory.class), ex);
                }
            }

            return factory;
        }
    }

    private void resolveExecutor(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object execTarget, String beanName) {
        Object resolved = this.resolveExpression(rabbitListener.executor());
        if (resolved instanceof TaskExecutor tex) {
            endpoint.setTaskExecutor(tex);
        } else {
            String execBeanName = this.resolveExpressionAsString(rabbitListener.executor(), "executor");
            if (StringUtils.hasText(execBeanName)) {
                try {
                    endpoint.setTaskExecutor((TaskExecutor) this.getBeanFactory().getBean(execBeanName, TaskExecutor.class));
                } catch (NoSuchBeanDefinitionException ex) {
                    throw new BeanInitializationException(noBeanFoundMessage(execTarget, beanName, execBeanName, TaskExecutor.class), ex);
                }
            }
        }

    }

    private void resolvePostProcessor(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object target, String beanName) {
        Object resolved = this.resolveExpression(rabbitListener.replyPostProcessor());
        if (resolved instanceof ReplyPostProcessor rpp) {
            endpoint.setReplyPostProcessor(rpp);
        } else {
            String ppBeanName = this.resolveExpressionAsString(rabbitListener.replyPostProcessor(), "replyPostProcessor");
            if (StringUtils.hasText(ppBeanName)) {
                try {
                    endpoint.setReplyPostProcessor((ReplyPostProcessor) this.getBeanFactory().getBean(ppBeanName, ReplyPostProcessor.class));
                } catch (NoSuchBeanDefinitionException ex) {
                    throw new BeanInitializationException(noBeanFoundMessage(target, beanName, ppBeanName, ReplyPostProcessor.class), ex);
                }
            }
        }

    }

    private void resolveMessageConverter(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object target, String beanName) {
        Object resolved = this.resolveExpression(rabbitListener.messageConverter());
        if (resolved instanceof MessageConverter converter) {
            endpoint.setMessageConverter(converter);
        } else {
            String mcBeanName = this.resolveExpressionAsString(rabbitListener.messageConverter(), "messageConverter");
            if (StringUtils.hasText(mcBeanName)) {
                try {
                    endpoint.setMessageConverter((MessageConverter) this.getBeanFactory().getBean(mcBeanName, MessageConverter.class));
                } catch (NoSuchBeanDefinitionException ex) {
                    throw new BeanInitializationException(noBeanFoundMessage(target, beanName, mcBeanName, MessageConverter.class), ex);
                }
            }
        }

    }

    private void resolveReplyContentType(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener) {
        String contentType = this.resolveExpressionAsString(rabbitListener.replyContentType(), "replyContentType");
        if (StringUtils.hasText(contentType)) {
            endpoint.setReplyContentType(contentType);
            endpoint.setConverterWinsContentType(this.resolveExpressionAsBoolean(rabbitListener.converterWinsContentType()));
        }

    }

    private @Nullable String getEndpointId(RabbitListener rabbitListener) {
        return StringUtils.hasText(rabbitListener.id()) ? this.resolveExpressionAsString(rabbitListener.id(), "id") : "org.springframework.amqp.rabbit.RabbitListenerEndpointContainer#" + this.counter.getAndIncrement();
    }

    private List<Object> resolveQueues(RabbitListener rabbitListener, Collection<Declarable> declarables) {
        String[] queues = rabbitListener.queues();
        QueueBinding[] bindings = rabbitListener.bindings();
        org.springframework.amqp.rabbit.annotation.Queue[] queuesToDeclare = rabbitListener.queuesToDeclare();
        List<String> queueNames = new ArrayList(queues.length);
        List<Queue> queueBeans = new ArrayList(queues.length);

        for (String queue : queues) {
            this.resolveQueues(queue, queueNames, queueBeans);
        }

        if (!queueNames.isEmpty()) {
            queueBeans.forEach((qb) -> queueNames.add(qb.getName()));
            queueBeans.clear();
        }

        if (queuesToDeclare.length > 0) {
            if (queues.length > 0) {
                throw new BeanInitializationException("@RabbitListener can have only one of 'queues', 'queuesToDeclare', or 'bindings'");
            }

            for (org.springframework.amqp.rabbit.annotation.Queue queue : queuesToDeclare) {
                queueNames.add(this.declareQueue(queue, declarables));
            }
        }

        if (bindings.length > 0) {
            if (queues.length <= 0 && queuesToDeclare.length <= 0) {
                return (List) Arrays.stream(this.registerBeansForDeclaration(rabbitListener, declarables)).map((s) -> s).collect(Collectors.toList());
            } else {
                throw new BeanInitializationException("@RabbitListener can have only one of 'queues', 'queuesToDeclare', or 'bindings'");
            }
        } else {
            return queueNames.isEmpty() ? (List) queueBeans.stream().map((s) -> s).collect(Collectors.toList()) : (List) queueNames.stream().map((s) -> s).collect(Collectors.toList());
        }
    }

    private void resolveQueues(String queue, List<String> result, List<Queue> queueBeans) {
        this.resolveAsStringOrQueue(this.resolveExpression(queue), result, queueBeans, "queues");
    }

    private void resolveAsStringOrQueue(@Nullable Object resolvedValue, List<String> names, @Nullable List<Queue> queues, String what) {
        Object resolvedValueToUse = resolvedValue;
        if (resolvedValue instanceof String[] strings) {
            resolvedValueToUse = Arrays.asList(strings);
        }

        if (queues != null && resolvedValueToUse instanceof Queue q) {
            if (!names.isEmpty()) {
                names.add(q.getName());
            } else {
                queues.add(q);
            }
        } else if (resolvedValueToUse instanceof String str) {
            names.add(str);
        } else {
            if (!(resolvedValueToUse instanceof Iterable)) {
                throw new IllegalArgumentException(String.format("@RabbitListener." + what + " can't resolve '%s' as a String[] or a String " + (queues != null ? "or a Queue" : ""), resolvedValue));
            }

            for (Object object : (Iterable) resolvedValueToUse) {
                this.resolveAsStringOrQueue(object, names, queues, what);
            }
        }

    }

    private String[] registerBeansForDeclaration(RabbitListener rabbitListener, Collection<Declarable> declarables) {
        List<String> queues = new ArrayList();
        if (this.getBeanFactory() instanceof ConfigurableBeanFactory) {
            for (QueueBinding binding : rabbitListener.bindings()) {
                String queueName = this.declareQueue(binding.value(), declarables);
                queues.add(queueName);
                this.declareExchangeAndBinding(binding, queueName, declarables);
            }
        }

        return (String[]) queues.toArray(new String[0]);
    }

    private String declareQueue(org.springframework.amqp.rabbit.annotation.Queue bindingQueue, Collection<Declarable> declarables) {
        String queueName = (String) this.resolveExpression(bindingQueue.value());
        boolean isAnonymous = false;
        if (!StringUtils.hasText(queueName)) {
            queueName = Base64UrlNamingStrategy.DEFAULT.generateName();
            isAnonymous = true;
        }

        Queue queue = new Queue(queueName, this.resolveExpressionAsBoolean(bindingQueue.durable(), !isAnonymous), this.resolveExpressionAsBoolean(bindingQueue.exclusive(), isAnonymous), this.resolveExpressionAsBoolean(bindingQueue.autoDelete(), isAnonymous), this.resolveArguments(bindingQueue.arguments()));
        queue.setIgnoreDeclarationExceptions(this.resolveExpressionAsBoolean(bindingQueue.ignoreDeclarationExceptions()));
        ((ConfigurableBeanFactory) this.getBeanFactory()).registerSingleton(queueName + ++this.increment, queue);
        if (bindingQueue.admins().length > 0) {
            queue.setAdminsThatShouldDeclare((Object[]) bindingQueue.admins());
        }

        queue.setShouldDeclare(this.resolveExpressionAsBoolean(bindingQueue.declare()));
        declarables.add(queue);
        return queueName;
    }

    private void declareExchangeAndBinding(QueueBinding binding, String queueName, Collection<Declarable> declarables) {
        Exchange bindingExchange = binding.exchange();
        String exchangeName = this.resolveExpressionAsString(bindingExchange.value(), "@Exchange.exchange");
        Assert.isTrue(StringUtils.hasText(exchangeName), () -> "Exchange name required; binding queue " + queueName);
        String exchangeType = this.resolveExpressionAsString(bindingExchange.type(), "@Exchange.type");
        ExchangeBuilder exchangeBuilder = new ExchangeBuilder(exchangeName, exchangeType);
        if (this.resolveExpressionAsBoolean(bindingExchange.autoDelete())) {
            exchangeBuilder.autoDelete();
        }

        if (this.resolveExpressionAsBoolean(bindingExchange.internal())) {
            exchangeBuilder.internal();
        }

        if (this.resolveExpressionAsBoolean(bindingExchange.delayed())) {
            exchangeBuilder.delayed();
        }

        if (this.resolveExpressionAsBoolean(bindingExchange.ignoreDeclarationExceptions())) {
            exchangeBuilder.ignoreDeclarationExceptions();
        }

        if (!this.resolveExpressionAsBoolean(bindingExchange.declare())) {
            exchangeBuilder.suppressDeclaration();
        }

        if (bindingExchange.admins().length > 0) {
            exchangeBuilder.admins((Object[]) bindingExchange.admins());
        }

        Map<String, Object> arguments = this.resolveArguments(bindingExchange.arguments());
        if (!CollectionUtils.isEmpty(arguments)) {
            exchangeBuilder.withArguments(arguments);
        }

        org.springframework.amqp.core.Exchange exchange = ((ExchangeBuilder) exchangeBuilder.durable(this.resolveExpressionAsBoolean(bindingExchange.durable()))).build();
        ((ConfigurableBeanFactory) this.getBeanFactory()).registerSingleton(exchangeName + ++this.increment, exchange);
        this.registerBindings(binding, queueName, exchangeName, exchangeType, declarables);
        declarables.add(exchange);
    }

    private void registerBindings(QueueBinding binding, String queueName, String exchangeName, String exchangeType, Collection<Declarable> declarables) {
        List<String> routingKeys;
        if (!exchangeType.equals("fanout") && binding.key().length != 0) {
            int length = binding.key().length;
            routingKeys = new ArrayList(length);

            for (int i = 0; i < length; ++i) {
                this.resolveAsStringOrQueue(this.resolveExpression(binding.key()[i]), routingKeys, (List) null, "@QueueBinding.key");
            }
        } else {
            routingKeys = Collections.singletonList("");
        }

        Map<String, Object> bindingArguments = this.resolveArguments(binding.arguments());
        boolean bindingIgnoreExceptions = this.resolveExpressionAsBoolean(binding.ignoreDeclarationExceptions());
        boolean declare = this.resolveExpressionAsBoolean(binding.declare());

        for (String routingKey : routingKeys) {
            Binding actualBinding = new Binding(queueName, DestinationType.QUEUE, exchangeName, routingKey, bindingArguments);
            actualBinding.setIgnoreDeclarationExceptions(bindingIgnoreExceptions);
            actualBinding.setShouldDeclare(declare);
            if (binding.admins().length > 0) {
                actualBinding.setAdminsThatShouldDeclare((Object[]) binding.admins());
            }

            ((ConfigurableBeanFactory) this.getBeanFactory()).registerSingleton(exchangeName + "." + queueName + ++this.increment, actualBinding);
            declarables.add(actualBinding);
        }

    }

    private Map<String, Object> resolveArguments(Argument[] arguments) {
        Map<String, Object> map = new HashMap();

        for (Argument arg : arguments) {
            String key = this.resolveExpressionAsString(arg.name(), "@Argument.name");
            if (StringUtils.hasText(key)) {
                Object value = this.resolveExpression(arg.value());
                Object type = this.resolveExpression(arg.type());
                Class<?> typeClass;
                String typeName;
                if (type instanceof Class) {
                    Class<?> clazz = (Class) type;
                    typeClass = clazz;
                    typeName = clazz.getName();
                } else {
                    Assert.isTrue(type instanceof String, () -> "Type must resolve to a Class or String, but resolved to [" + String.valueOf(type) + "]");
                    typeName = (String) type;

                    try {
                        typeClass = ClassUtils.forName(typeName, this.getBeanClassLoader());
                    } catch (Exception e) {
                        throw new IllegalStateException("Could not load class", e);
                    }
                }

                this.addToMap(map, key, value == null ? "" : value, typeClass, typeName);
            } else {
                this.logger.debug("@Argument ignored because the name resolved to an empty String");
            }
        }

        return map.isEmpty() ? null : map;
    }

    private void addToMap(Map<String, @Nullable Object> map, String key, Object value, Class<?> typeClass, String typeName) {
        if (value.getClass().getName().equals(typeName)) {
            if (typeClass.equals(String.class) && !StringUtils.hasText((String) value)) {
                this.putEmpty(map, key);
            } else {
                map.put(key, value);
            }
        } else {
            if (value instanceof String) {
                String string = (String) value;
                if (!StringUtils.hasText(string)) {
                    this.putEmpty(map, key);
                    return;
                }
            }

            if (!CONVERSION_SERVICE.canConvert(value.getClass(), typeClass)) {
                String var10002 = value.getClass().getName();
                throw new IllegalStateException("Cannot convert from " + var10002 + " to " + typeName);
            }

            map.put(key, CONVERSION_SERVICE.convert(value, typeClass));
        }

    }

    private void putEmpty(Map<String, @Nullable Object> map, String key) {
        if (this.emptyStringArguments.contains(key)) {
            map.put(key, "");
        } else {
            map.put(key, (Object) null);
        }

    }

    private class RabbitHandlerMethodFactoryAdapter implements MessageHandlerMethodFactory {
        private final DefaultFormattingConversionService defaultFormattingConversionService = new DefaultFormattingConversionService();
        private @Nullable MessageHandlerMethodFactory factory;

        RabbitHandlerMethodFactoryAdapter() {
        }

        public void setMessageHandlerMethodFactory(MessageHandlerMethodFactory rabbitHandlerMethodFactory1) {
            this.factory = rabbitHandlerMethodFactory1;
        }

        public InvocableHandlerMethod createInvocableHandlerMethod(Object bean, Method method) {
            return this.getFactory().createInvocableHandlerMethod(bean, method);
        }

        private MessageHandlerMethodFactory getFactory() {
            if (this.factory == null) {
                this.factory = this.createDefaultMessageHandlerMethodFactory();
            }

            return this.factory;
        }

        private MessageHandlerMethodFactory createDefaultMessageHandlerMethodFactory() {
            DefaultMessageHandlerMethodFactory defaultFactory = new AmqpMessageHandlerMethodFactory();
            Validator validator = RabbitListenerAnnotationBeanPostProcessor.this.registrar.getValidator();
            if (validator != null) {
                defaultFactory.setValidator(validator);
            }

            defaultFactory.setBeanFactory(RabbitListenerAnnotationBeanPostProcessor.this.getBeanFactory());
            this.defaultFormattingConversionService.addConverter(new BytesToStringConverter(RabbitListenerAnnotationBeanPostProcessor.this.charset));
            defaultFactory.setConversionService(this.defaultFormattingConversionService);
            List<HandlerMethodArgumentResolver> customArgumentsResolver = new ArrayList(RabbitListenerAnnotationBeanPostProcessor.this.registrar.getCustomMethodArgumentResolvers());
            defaultFactory.setCustomArgumentResolvers(customArgumentsResolver);
            defaultFactory.setMessageConverter(new GenericMessageConverter(this.defaultFormattingConversionService));
            defaultFactory.afterPropertiesSet();
            return defaultFactory;
        }
    }
}

