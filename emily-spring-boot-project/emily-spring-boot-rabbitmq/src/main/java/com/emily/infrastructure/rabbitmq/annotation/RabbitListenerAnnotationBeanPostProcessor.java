package com.emily.infrastructure.rabbitmq.annotation;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jspecify.annotations.Nullable;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Base64UrlNamingStrategy;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.MethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.MultiMethodRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.adapter.AmqpMessageHandlerMethodFactory;
import org.springframework.amqp.rabbit.listener.adapter.ReplyPostProcessor;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
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
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Validator;

public class RabbitListenerAnnotationBeanPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware, BeanClassLoaderAware, EnvironmentAware, SmartInitializingSingleton {
    public static final String DEFAULT_RABBIT_LISTENER_CONTAINER_FACTORY_BEAN_NAME = "rabbitListenerContainerFactory";
    public static final String RABBIT_EMPTY_STRING_ARGUMENTS_PROPERTY = "spring.rabbitmq.emptyStringArguments";
    private static final ConversionService CONVERSION_SERVICE = new DefaultConversionService();
    private final Log logger = LogFactory.getLog(this.getClass());
    private final Set<String> emptyStringArguments = new HashSet();
    private @Nullable RabbitListenerEndpointRegistry endpointRegistry;
    private String defaultContainerFactoryBeanName = "rabbitListenerContainerFactory";
    private BeanFactory beanFactory;
    private ClassLoader beanClassLoader;
    private final RabbitHandlerMethodFactoryAdapter messageHandlerMethodFactory = new RabbitHandlerMethodFactoryAdapter();
    private final RabbitListenerEndpointRegistrar registrar = new RabbitListenerEndpointRegistrar();
    private final AtomicInteger counter = new AtomicInteger();
    private final ConcurrentMap<Class<?>, TypeMetadata> typeCache = new ConcurrentHashMap();
    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();
    private BeanExpressionContext expressionContext;
    private int increment;
    private Charset charset;

    public int getOrder() {
        return Integer.MAX_VALUE;
    }

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

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory clbf) {
            BeanExpressionResolver beanExpressionResolver = clbf.getBeanExpressionResolver();
            if (beanExpressionResolver != null) {
                this.resolver = beanExpressionResolver;
            }

            this.expressionContext = new BeanExpressionContext(clbf, (Scope)null);
        }

    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    public void setEnvironment(Environment environment) {
        String property = (String)environment.getProperty("spring.rabbitmq.emptyStringArguments", String.class);
        if (property != null) {
            this.emptyStringArguments.addAll(StringUtils.commaDelimitedListToSet(property));
        }

    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    MessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        return this.messageHandlerMethodFactory;
    }

    public void afterSingletonsInstantiated() {
        this.registrar.setBeanFactory(this.beanFactory);
        BeanFactory var2 = this.beanFactory;
        if (var2 instanceof ListableBeanFactory lbf) {
            Map<String, RabbitListenerConfigurer> instances = lbf.getBeansOfType(RabbitListenerConfigurer.class, false, false);
            Iterator var3 = instances.values().iterator();

            while(var3.hasNext()) {
                RabbitListenerConfigurer configurer = (RabbitListenerConfigurer)var3.next();
                configurer.configureRabbitListeners(this.registrar);
            }
        }

        if (this.registrar.getEndpointRegistry() == null) {
            if (this.endpointRegistry == null) {
                this.endpointRegistry = (RabbitListenerEndpointRegistry)this.beanFactory.getBean("org.springframework.amqp.rabbit.config.internalRabbitListenerEndpointRegistry", RabbitListenerEndpointRegistry.class);
            }

            this.registrar.setEndpointRegistry(this.endpointRegistry);
        }

        this.registrar.setContainerFactoryBeanName(this.defaultContainerFactoryBeanName);
        MessageHandlerMethodFactory handlerMethodFactory = this.registrar.getMessageHandlerMethodFactory();
        if (handlerMethodFactory != null) {
            this.messageHandlerMethodFactory.setMessageHandlerMethodFactory(handlerMethodFactory);
        }

        this.registrar.afterPropertiesSet();
        this.typeCache.clear();
    }

    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        TypeMetadata metadata = (TypeMetadata)this.typeCache.computeIfAbsent(targetClass, this::buildMetadata);
        ListenerMethod[] var5 = metadata.listenerMethods;
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            ListenerMethod lm = var5[var7];
            RabbitListener[] var9 = lm.annotations;
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                RabbitListener rabbitListener = var9[var11];
                this.processAmqpListener(rabbitListener, lm.method, bean, beanName);
            }
        }

        if (metadata.handlerMethods.length > 0) {
            this.processMultiMethodListeners(metadata.classAnnotations, metadata.handlerMethods, bean, beanName);
        }

        return bean;
    }

    private TypeMetadata buildMetadata(Class<?> targetClass) {
        List<RabbitListener> classLevelListeners = this.findListenerAnnotations(targetClass);
        boolean hasClassLevelListeners = !classLevelListeners.isEmpty();
        List<ListenerMethod> methods = new ArrayList();
        List<Method> multiMethods = new ArrayList();
        ReflectionUtils.doWithMethods(targetClass, (method) -> {
            List<RabbitListener> listenerAnnotations = this.findListenerAnnotations(method);
            if (!listenerAnnotations.isEmpty()) {
                methods.add(new ListenerMethod(method, (RabbitListener[])listenerAnnotations.toArray(new RabbitListener[0])));
            }

            if (hasClassLevelListeners) {
                RabbitHandler rabbitHandler = (RabbitHandler)AnnotationUtils.findAnnotation(method, RabbitHandler.class);
                if (rabbitHandler != null) {
                    multiMethods.add(method);
                }
            }

        }, ReflectionUtils.USER_DECLARED_METHODS.and((meth) -> {
            return !meth.getDeclaringClass().getName().contains("$MockitoMock$");
        }));
        return methods.isEmpty() && multiMethods.isEmpty() ? TypeMetadata.EMPTY : new TypeMetadata((ListenerMethod[])methods.toArray(new ListenerMethod[0]), (Method[])multiMethods.toArray(new Method[0]), (RabbitListener[])classLevelListeners.toArray(new RabbitListener[0]));
    }

    private List<RabbitListener> findListenerAnnotations(AnnotatedElement element) {
        return (List)MergedAnnotations.from(element, SearchStrategy.TYPE_HIERARCHY).stream(RabbitListener.class).filter((tma) -> {
            Object source = tma.getSource();
            String name = "";
            if (source instanceof Class<?> clazz) {
                name = clazz.getName();
            } else if (source instanceof Method method) {
                name = method.getDeclaringClass().getName();
            }

            return !name.contains("$MockitoMock$");
        }).map(MergedAnnotation::synthesize).collect(Collectors.toList());
    }

    private void processMultiMethodListeners(RabbitListener[] classLevelListeners, Method[] multiMethods, Object bean, String beanName) {
        List<Method> checkedMethods = new ArrayList(multiMethods.length);
        Method defaultMethod = null;
        Method[] var7 = multiMethods;
        int var8 = multiMethods.length;

        int var9;
        for(var9 = 0; var9 < var8; ++var9) {
            Method method = var7[var9];
            Method checked = this.checkProxy(method, bean);
            RabbitHandler annotation = (RabbitHandler)AnnotationUtils.findAnnotation(method, RabbitHandler.class);
            if (annotation != null && annotation.isDefault()) {
                Method toAssert = defaultMethod;
                Assert.state(toAssert == null, () -> {
                    String var10000 = String.valueOf(toAssert);
                    return "Only one @RabbitHandler can be marked 'isDefault', found: " + var10000 + " and " + String.valueOf(method);
                });
                defaultMethod = checked;
            }

            checkedMethods.add(checked);
        }

        RabbitListener[] var14 = classLevelListeners;
        var8 = classLevelListeners.length;

        for(var9 = 0; var9 < var8; ++var9) {
            RabbitListener classLevelListener = var14[var9];
            MultiMethodRabbitListenerEndpoint endpoint = new MultiMethodRabbitListenerEndpoint(checkedMethods, defaultMethod, bean);
            this.processListener(endpoint, classLevelListener, bean, bean.getClass(), beanName);
        }

    }

    protected Collection<Declarable> processAmqpListener(RabbitListener rabbitListener, Method method, Object bean, String beanName) {
        Method methodToUse = this.checkProxy(method, bean);
        MethodRabbitListenerEndpoint endpoint = new MethodRabbitListenerEndpoint();
        endpoint.setMethod(methodToUse);
        return this.processListener(endpoint, rabbitListener, bean, methodToUse, beanName);
    }

    private Method checkProxy(Method methodArg, Object bean) {
        Method method = methodArg;
        if (AopUtils.isJdkDynamicProxy(bean)) {
            try {
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised)bean).getProxiedInterfaces();
                Class[] var5 = proxiedInterfaces;
                int var6 = proxiedInterfaces.length;
                int var7 = 0;

                while(var7 < var6) {
                    Class<?> iface = var5[var7];

                    try {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException var10) {
                        ++var7;
                    }
                }
            } catch (SecurityException var11) {
                ReflectionUtils.handleReflectionException(var11);
            } catch (NoSuchMethodException var12) {
                NoSuchMethodException ex = var12;
                throw new IllegalStateException(String.format("@RabbitListener method '%s' found on bean target class '%s', but not found in any interface(s) for a bean JDK proxy. Either pull the method up to an interface or switch to subclass (CGLIB) proxies by setting proxy-target-class/proxyTargetClass attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()), ex);
            }
        }

        return method;
    }

    protected Collection<Declarable> processListener(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener, Object bean, Object target, String beanName) {
        List<Declarable> declarables = new ArrayList();
        endpoint.setBean(bean);
        endpoint.setMessageHandlerMethodFactory(this.messageHandlerMethodFactory);
        endpoint.setId(this.getEndpointId(rabbitListener));
        List<Object> resolvedQueues = this.resolveQueues(rabbitListener, declarables);
        if (!resolvedQueues.isEmpty()) {
            if (resolvedQueues.get(0) instanceof String) {
                endpoint.setQueueNames((String[])resolvedQueues.stream().map((o) -> {
                    return (String)o;
                }).toArray((x$0) -> {
                    return new String[x$0];
                }));
            } else {
                endpoint.setQueues((Queue[])resolvedQueues.stream().map((o) -> {
                    return (Queue)o;
                }).toArray((x$0) -> {
                    return new Queue[x$0];
                }));
            }
        }

        endpoint.setConcurrency(this.resolveExpressionAsStringOrInteger(rabbitListener.concurrency(), "concurrency"));
        endpoint.setBeanFactory(this.beanFactory);
        endpoint.setReturnExceptions(this.resolveExpressionAsBoolean(rabbitListener.returnExceptions()));
        this.resolveErrorHandler(endpoint, rabbitListener);
        String group = rabbitListener.group();
        String priority;
        if (StringUtils.hasText(group)) {
            Object resolvedGroup = this.resolveExpression(group);
            if (resolvedGroup instanceof String) {
                priority = (String)resolvedGroup;
                endpoint.setGroup(priority);
            }
        }

        String autoStartup = rabbitListener.autoStartup();
        if (StringUtils.hasText(autoStartup)) {
            endpoint.setAutoStartup(this.resolveExpressionAsBoolean(autoStartup));
        }

        endpoint.setExclusive(rabbitListener.exclusive());
        priority = this.resolveExpressionAsString(rabbitListener.priority(), "priority");
        if (StringUtils.hasText(priority)) {
            try {
                endpoint.setPriority(Integer.valueOf(priority));
            } catch (NumberFormatException var12) {
                NumberFormatException ex = var12;
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
                endpoint.setErrorHandler((RabbitListenerErrorHandler)this.beanFactory.getBean(errorHandlerBeanName, RabbitListenerErrorHandler.class));
            }
        }

    }

    private void resolveAckMode(MethodRabbitListenerEndpoint endpoint, RabbitListener rabbitListener) {
        String ackModeAttr = rabbitListener.ackMode();
        if (StringUtils.hasText(ackModeAttr)) {
            Object ackMode = this.resolveExpression(ackModeAttr);
            if (ackMode instanceof String) {
                String str = (String)ackMode;
                endpoint.setAckMode(AcknowledgeMode.valueOf(str));
            } else if (ackMode instanceof AcknowledgeMode) {
                AcknowledgeMode mode = (AcknowledgeMode)ackMode;
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
                    endpoint.setAdmin((AmqpAdmin)this.beanFactory.getBean(rabbitAdmin, RabbitAdmin.class));
                } catch (NoSuchBeanDefinitionException var8) {
                    NoSuchBeanDefinitionException ex = var8;
                    String var10002 = String.valueOf(adminTarget);
                    throw new BeanInitializationException("Could not register rabbit listener endpoint on [" + var10002 + "], no " + RabbitAdmin.class.getSimpleName() + " with id '" + rabbitAdmin + "' was found in the application context", ex);
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
                    factory = (RabbitListenerContainerFactory)this.beanFactory.getBean(containerFactoryBeanName, RabbitListenerContainerFactory.class);
                } catch (NoSuchBeanDefinitionException var8) {
                    NoSuchBeanDefinitionException ex = var8;
                    throw new BeanInitializationException(this.noBeanFoundMessage(factoryTarget, beanName, containerFactoryBeanName, RabbitListenerContainerFactory.class), ex);
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
                    endpoint.setTaskExecutor((TaskExecutor)this.beanFactory.getBean(execBeanName, TaskExecutor.class));
                } catch (NoSuchBeanDefinitionException var9) {
                    NoSuchBeanDefinitionException ex = var9;
                    throw new BeanInitializationException(this.noBeanFoundMessage(execTarget, beanName, execBeanName, TaskExecutor.class), ex);
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
                    endpoint.setReplyPostProcessor((ReplyPostProcessor)this.beanFactory.getBean(ppBeanName, ReplyPostProcessor.class));
                } catch (NoSuchBeanDefinitionException var9) {
                    NoSuchBeanDefinitionException ex = var9;
                    throw new BeanInitializationException(this.noBeanFoundMessage(target, beanName, ppBeanName, ReplyPostProcessor.class), ex);
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
                    endpoint.setMessageConverter((MessageConverter)this.beanFactory.getBean(mcBeanName, MessageConverter.class));
                } catch (NoSuchBeanDefinitionException var9) {
                    NoSuchBeanDefinitionException ex = var9;
                    throw new BeanInitializationException(this.noBeanFoundMessage(target, beanName, mcBeanName, MessageConverter.class), ex);
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

    protected String noBeanFoundMessage(Object target, String listenerBeanName, String requestedBeanName, Class<?> expectedClass) {
        String var10000 = String.valueOf(target);
        return "Could not register rabbit listener endpoint on [" + var10000 + "] for bean " + listenerBeanName + ", no '" + expectedClass.getSimpleName() + "' with id '" + requestedBeanName + "' was found in the application context";
    }

    private String getEndpointId(RabbitListener rabbitListener) {
        return StringUtils.hasText(rabbitListener.id()) ? this.resolveExpressionAsString(rabbitListener.id(), "id") : "org.springframework.amqp.rabbit.RabbitListenerEndpointContainer#" + this.counter.getAndIncrement();
    }

    private List<Object> resolveQueues(RabbitListener rabbitListener, Collection<Declarable> declarables) {
        String[] queues = rabbitListener.queues();
        QueueBinding[] bindings = rabbitListener.bindings();
        org.springframework.amqp.rabbit.annotation.Queue[] queuesToDeclare = rabbitListener.queuesToDeclare();
        List<String> queueNames = new ArrayList(queues.length);
        List<Queue> queueBeans = new ArrayList(queues.length);
        String[] var8 = queues;
        int var9 = queues.length;

        int var10;
        for(var10 = 0; var10 < var9; ++var10) {
            String queue = var8[var10];
            this.resolveQueues(queue, queueNames, queueBeans);
        }

        if (!queueNames.isEmpty()) {
            queueBeans.forEach((qb) -> {
                queueNames.add(qb.getName());
            });
            queueBeans.clear();
        }

        if (queuesToDeclare.length > 0) {
            if (queues.length > 0) {
                throw new BeanInitializationException("@RabbitListener can have only one of 'queues', 'queuesToDeclare', or 'bindings'");
            }

            org.springframework.amqp.rabbit.annotation.Queue[] var12 = queuesToDeclare;
            var9 = queuesToDeclare.length;

            for(var10 = 0; var10 < var9; ++var10) {
                org.springframework.amqp.rabbit.annotation.Queue queue = var12[var10];
                queueNames.add(this.declareQueue(queue, declarables));
            }
        }

        if (bindings.length > 0) {
            if (queues.length <= 0 && queuesToDeclare.length <= 0) {
                return (List)Arrays.stream(this.registerBeansForDeclaration(rabbitListener, declarables)).map((s) -> {
                    return s;
                }).collect(Collectors.toList());
            } else {
                throw new BeanInitializationException("@RabbitListener can have only one of 'queues', 'queuesToDeclare', or 'bindings'");
            }
        } else {
            return queueNames.isEmpty() ? (List)queueBeans.stream().map((s) -> {
                return s;
            }).collect(Collectors.toList()) : (List)queueNames.stream().map((s) -> {
                return s;
            }).collect(Collectors.toList());
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

            Iterator var8 = ((Iterable)resolvedValueToUse).iterator();

            while(var8.hasNext()) {
                Object object = var8.next();
                this.resolveAsStringOrQueue(object, names, queues, what);
            }
        }

    }

    private String[] registerBeansForDeclaration(RabbitListener rabbitListener, Collection<Declarable> declarables) {
        List<String> queues = new ArrayList();
        if (this.beanFactory instanceof ConfigurableBeanFactory) {
            QueueBinding[] var4 = rabbitListener.bindings();
            int var5 = var4.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                QueueBinding binding = var4[var6];
                String queueName = this.declareQueue(binding.value(), declarables);
                queues.add(queueName);
                this.declareExchangeAndBinding(binding, queueName, declarables);
            }
        }

        return (String[])queues.toArray(new String[0]);
    }

    private String declareQueue(org.springframework.amqp.rabbit.annotation.Queue bindingQueue, Collection<Declarable> declarables) {
        String queueName = (String)this.resolveExpression(bindingQueue.value());
        boolean isAnonymous = false;
        if (!StringUtils.hasText(queueName)) {
            queueName = Base64UrlNamingStrategy.DEFAULT.generateName();
            isAnonymous = true;
        }

        Queue queue = new Queue(queueName, this.resolveExpressionAsBoolean(bindingQueue.durable(), !isAnonymous), this.resolveExpressionAsBoolean(bindingQueue.exclusive(), isAnonymous), this.resolveExpressionAsBoolean(bindingQueue.autoDelete(), isAnonymous), this.resolveArguments(bindingQueue.arguments()));
        queue.setIgnoreDeclarationExceptions(this.resolveExpressionAsBoolean(bindingQueue.ignoreDeclarationExceptions()));
        ((ConfigurableBeanFactory)this.beanFactory).registerSingleton(queueName + ++this.increment, queue);
        if (bindingQueue.admins().length > 0) {
            queue.setAdminsThatShouldDeclare((Object[])bindingQueue.admins());
        }

        queue.setShouldDeclare(this.resolveExpressionAsBoolean(bindingQueue.declare()));
        declarables.add(queue);
        return queueName;
    }

    private void declareExchangeAndBinding(QueueBinding binding, String queueName, Collection<Declarable> declarables) {
        Exchange bindingExchange = binding.exchange();
        String exchangeName = this.resolveExpressionAsString(bindingExchange.value(), "@Exchange.exchange");
        Assert.isTrue(StringUtils.hasText(exchangeName), () -> {
            return "Exchange name required; binding queue " + queueName;
        });
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
            exchangeBuilder.admins((Object[])bindingExchange.admins());
        }

        Map<String, Object> arguments = this.resolveArguments(bindingExchange.arguments());
        if (!CollectionUtils.isEmpty(arguments)) {
            exchangeBuilder.withArguments(arguments);
        }

        org.springframework.amqp.core.Exchange exchange = ((ExchangeBuilder)exchangeBuilder.durable(this.resolveExpressionAsBoolean(bindingExchange.durable()))).build();
        ((ConfigurableBeanFactory)this.beanFactory).registerSingleton(exchangeName + ++this.increment, exchange);
        this.registerBindings(binding, queueName, exchangeName, exchangeType, declarables);
        declarables.add(exchange);
    }

    private void registerBindings(QueueBinding binding, String queueName, String exchangeName, String exchangeType, Collection<Declarable> declarables) {
        Object routingKeys;
        if (!exchangeType.equals("fanout") && binding.key().length != 0) {
            int length = binding.key().length;
            routingKeys = new ArrayList(length);

            for(int i = 0; i < length; ++i) {
                this.resolveAsStringOrQueue(this.resolveExpression(binding.key()[i]), (List)routingKeys, (List)null, "@QueueBinding.key");
            }
        } else {
            routingKeys = Collections.singletonList("");
        }

        Map<String, Object> bindingArguments = this.resolveArguments(binding.arguments());
        boolean bindingIgnoreExceptions = this.resolveExpressionAsBoolean(binding.ignoreDeclarationExceptions());
        boolean declare = this.resolveExpressionAsBoolean(binding.declare());
        Iterator var10 = ((List)routingKeys).iterator();

        while(var10.hasNext()) {
            String routingKey = (String)var10.next();
            Binding actualBinding = new Binding(queueName, DestinationType.QUEUE, exchangeName, routingKey, bindingArguments);
            actualBinding.setIgnoreDeclarationExceptions(bindingIgnoreExceptions);
            actualBinding.setShouldDeclare(declare);
            if (binding.admins().length > 0) {
                actualBinding.setAdminsThatShouldDeclare((Object[])binding.admins());
            }

            ((ConfigurableBeanFactory)this.beanFactory).registerSingleton(exchangeName + "." + queueName + ++this.increment, actualBinding);
            declarables.add(actualBinding);
        }

    }

    private Map<String, Object> resolveArguments(Argument[] arguments) {
        Map<String, Object> map = new HashMap();
        Argument[] var3 = arguments;
        int var4 = arguments.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Argument arg = var3[var5];
            String key = this.resolveExpressionAsString(arg.name(), "@Argument.name");
            if (StringUtils.hasText(key)) {
                Object value = this.resolveExpression(arg.value());
                Object type = this.resolveExpression(arg.type());
                Class typeClass;
                String typeName;
                if (type instanceof Class) {
                    Class<?> clazz = (Class)type;
                    typeClass = clazz;
                    typeName = typeClass.getName();
                } else {
                    Assert.isTrue(type instanceof String, () -> {
                        return "Type must resolve to a Class or String, but resolved to [" + String.valueOf(type) + "]";
                    });
                    typeName = (String)type;

                    try {
                        typeClass = ClassUtils.forName(typeName, this.beanClassLoader);
                    } catch (Exception var14) {
                        Exception e = var14;
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
            if (typeClass.equals(String.class) && !StringUtils.hasText((String)value)) {
                this.putEmpty(map, key);
            } else {
                map.put(key, value);
            }
        } else {
            if (value instanceof String) {
                String string = (String)value;
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
            map.put(key, (Object)null);
        }

    }

    private boolean resolveExpressionAsBoolean(String value) {
        return this.resolveExpressionAsBoolean(value, false);
    }

    private boolean resolveExpressionAsBoolean(String value, boolean defaultValue) {
        Object resolved = this.resolveExpression(value);
        if (resolved instanceof Boolean bool) {
            return bool;
        } else if (resolved instanceof String str) {
            return StringUtils.hasText(str) ? Boolean.parseBoolean(str) : defaultValue;
        } else {
            return defaultValue;
        }
    }

    protected String resolveExpressionAsString(String value, String attribute) {
        Object resolved = this.resolveExpression(value);
        if (resolved instanceof String str) {
            return str;
        } else {
            throw new IllegalStateException("The [" + attribute + "] must resolve to a String. Resolved to [" + String.valueOf(resolved) + "] for [" + value + "]");
        }
    }

    private @Nullable String resolveExpressionAsStringOrInteger(String value, String attribute) {
        if (!StringUtils.hasLength(value)) {
            return null;
        } else {
            Object resolved = this.resolveExpression(value);
            if (resolved instanceof String) {
                String str = (String)resolved;
                return str;
            } else if (resolved instanceof Integer) {
                return resolved.toString();
            } else {
                throw new IllegalStateException("The [" + attribute + "] must resolve to a String. Resolved to [" + String.valueOf(resolved) + "] for [" + value + "]");
            }
        }
    }

    protected @Nullable Object resolveExpression(String value) {
        String resolvedValue = this.resolve(value);
        return this.resolver.evaluate(resolvedValue, this.expressionContext);
    }

    private @Nullable String resolve(String value) {
        BeanFactory var3 = this.beanFactory;
        if (var3 instanceof ConfigurableBeanFactory cbf) {
            return cbf.resolveEmbeddedValue(value);
        } else {
            return value;
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

            defaultFactory.setBeanFactory(RabbitListenerAnnotationBeanPostProcessor.this.beanFactory);
            this.defaultFormattingConversionService.addConverter(new BytesToStringConverter(RabbitListenerAnnotationBeanPostProcessor.this.charset));
            defaultFactory.setConversionService(this.defaultFormattingConversionService);
            List<HandlerMethodArgumentResolver> customArgumentsResolver = new ArrayList(RabbitListenerAnnotationBeanPostProcessor.this.registrar.getCustomMethodArgumentResolvers());
            defaultFactory.setCustomArgumentResolvers(customArgumentsResolver);
            defaultFactory.setMessageConverter(new GenericMessageConverter(this.defaultFormattingConversionService));
            defaultFactory.afterPropertiesSet();
            return defaultFactory;
        }
    }

    private static class TypeMetadata {
        final ListenerMethod[] listenerMethods;
        final Method[] handlerMethods;
        final RabbitListener[] classAnnotations;
        static final TypeMetadata EMPTY = new TypeMetadata();

        private TypeMetadata() {
            this.listenerMethods = new ListenerMethod[0];
            this.handlerMethods = new Method[0];
            this.classAnnotations = new RabbitListener[0];
        }

        TypeMetadata(ListenerMethod[] methods, Method[] multiMethods, RabbitListener[] classLevelListeners) {
            this.listenerMethods = methods;
            this.handlerMethods = multiMethods;
            this.classAnnotations = classLevelListeners;
        }
    }

    private static record ListenerMethod(Method method, RabbitListener[] annotations) {
        private ListenerMethod(Method method, RabbitListener[] annotations) {
            this.method = method;
            this.annotations = annotations;
        }

        public Method method() {
            return this.method;
        }

        public RabbitListener[] annotations() {
            return this.annotations;
        }
    }

    private static record BytesToStringConverter(Charset charset) implements Converter<byte[], String> {
        private BytesToStringConverter(Charset charset) {
            this.charset = charset;
        }

        public String convert(byte[] source) {
            return new String(source, this.charset);
        }

        public Charset charset() {
            return this.charset;
        }
    }
}

