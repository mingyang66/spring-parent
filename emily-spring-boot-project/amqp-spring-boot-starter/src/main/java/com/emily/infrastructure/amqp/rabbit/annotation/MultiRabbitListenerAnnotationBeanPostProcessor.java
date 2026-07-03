package com.emily.infrastructure.amqp.rabbit.annotation;


import org.jspecify.annotations.Nullable;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.RabbitListenerConfigUtils;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

/**
 * {@link org.springframework.amqp.rabbit.annotation.MultiRabbitListenerAnnotationBeanPostProcessor}
 */
public class MultiRabbitListenerAnnotationBeanPostProcessor extends RabbitListenerAnnotationBeanPostProcessor {

    @Override
    protected Collection<Declarable> processAmqpListener(RabbitListener rabbitListener, Method method,
                                                         Object bean, String beanName) {
        final String rabbitAdmin = resolveMultiRabbitAdminName(rabbitListener);
        final RabbitListener rabbitListenerRef = proxyIfAdminNotPresent(rabbitListener, rabbitAdmin);
        final Collection<Declarable> declarables = super.processAmqpListener(rabbitListenerRef, method, bean, beanName);
        for (final Declarable declarable : declarables) {
            if (declarable.getDeclaringAdmins().isEmpty()) {
                declarable.setAdminsThatShouldDeclare(rabbitAdmin);
            }
        }
        return declarables;
    }

    private RabbitListener proxyIfAdminNotPresent(final RabbitListener rabbitListener, @Nullable String rabbitAdmin) {
        if (StringUtils.hasText(rabbitListener.admin())) {
            return rabbitListener;
        }
        return (RabbitListener) Proxy.newProxyInstance(
                RabbitListener.class.getClassLoader(), new Class<?>[]{RabbitListener.class},
                new RabbitListenerAdminReplacementInvocationHandler(rabbitListener, rabbitAdmin));
    }

    protected @Nullable String resolveMultiRabbitAdminName(RabbitListener rabbitListener) {

        var admin = rabbitListener.admin();
        if (StringUtils.hasText(admin)) {

            var resolved = super.resolveExpression(admin);
            if (resolved instanceof RabbitAdmin rabbitAdmin) {

                return rabbitAdmin.getBeanName();
            }

            return super.resolveExpressionAsString(admin, "admin");
        }

        var containerFactory = rabbitListener.containerFactory();
        if (StringUtils.hasText(containerFactory)) {

            var resolved = super.resolveExpression(containerFactory);
            if (resolved instanceof RabbitListenerContainerFactory<?> rlcf) {

                return rlcf.getBeanName() + RabbitListenerConfigUtils.MULTI_RABBIT_ADMIN_SUFFIX;
            }

            return resolved + RabbitListenerConfigUtils.MULTI_RABBIT_ADMIN_SUFFIX;
        }

        return RabbitListenerConfigUtils.RABBIT_ADMIN_BEAN_NAME;
    }

    private record RabbitListenerAdminReplacementInvocationHandler(RabbitListener target, @Nullable String admin)
            implements InvocationHandler {

        @Override
        public @Nullable Object invoke(final Object proxy, final Method method, final Object[] args)
                throws InvocationTargetException, IllegalAccessException {
            if (method.getName().equals("admin")) {
                return this.admin;
            }
            return method.invoke(this.target, args);
        }

    }
}
