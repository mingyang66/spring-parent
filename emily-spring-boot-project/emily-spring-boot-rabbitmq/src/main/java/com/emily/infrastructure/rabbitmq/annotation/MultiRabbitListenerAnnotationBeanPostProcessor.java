package com.emily.infrastructure.rabbitmq.annotation;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.util.StringUtils;

public class MultiRabbitListenerAnnotationBeanPostProcessor extends RabbitListenerAnnotationBeanPostProcessor {
    public MultiRabbitListenerAnnotationBeanPostProcessor() {
    }

    protected Collection<Declarable> processAmqpListener(RabbitListener rabbitListener, Method method, Object bean, String beanName) {
        String rabbitAdmin = this.resolveMultiRabbitAdminName(rabbitListener);
        RabbitListener rabbitListenerRef = this.proxyIfAdminNotPresent(rabbitListener, rabbitAdmin);
        Collection<Declarable> declarables = super.processAmqpListener(rabbitListenerRef, method, bean, beanName);
        Iterator var8 = declarables.iterator();

        while(var8.hasNext()) {
            Declarable declarable = (Declarable)var8.next();
            if (declarable.getDeclaringAdmins().isEmpty()) {
                declarable.setAdminsThatShouldDeclare(new Object[]{rabbitAdmin});
            }
        }

        return declarables;
    }

    private RabbitListener proxyIfAdminNotPresent(final RabbitListener rabbitListener, final String rabbitAdmin) {
        return StringUtils.hasText(rabbitListener.admin()) ? rabbitListener : (RabbitListener)Proxy.newProxyInstance(RabbitListener.class.getClassLoader(), new Class[]{RabbitListener.class}, new MultiRabbitListenerAnnotationBeanPostProcessor.RabbitListenerAdminReplacementInvocationHandler(rabbitListener, rabbitAdmin));
    }

    protected String resolveMultiRabbitAdminName(RabbitListener rabbitListener) {
        String admin = rabbitListener.admin();
        if (StringUtils.hasText(admin)) {
            Object resolved = super.resolveExpression(admin);
            if (resolved instanceof RabbitAdmin) {
                RabbitAdmin rabbitAdmin = (RabbitAdmin)resolved;
                return rabbitAdmin.getBeanName();
            } else {
                return super.resolveExpressionAsString(admin, "admin");
            }
        } else {
            String containerFactory = rabbitListener.containerFactory();
            if (StringUtils.hasText(containerFactory)) {
                Object resolved = super.resolveExpression(containerFactory);
                if (resolved instanceof RabbitListenerContainerFactory) {
                    RabbitListenerContainerFactory<?> rlcf = (RabbitListenerContainerFactory)resolved;
                    return rlcf.getBeanName() + "-admin";
                } else {
                    return String.valueOf(resolved) + "-admin";
                }
            } else {
                return "amqpAdmin";
            }
        }
    }

    private static record RabbitListenerAdminReplacementInvocationHandler(RabbitListener target, String admin) implements InvocationHandler {
        private RabbitListenerAdminReplacementInvocationHandler(RabbitListener target, String admin) {
            this.target = target;
            this.admin = admin;
        }

        public Object invoke(final Object proxy, final Method method, final Object[] args) throws InvocationTargetException, IllegalAccessException {
            return method.getName().equals("admin") ? this.admin : method.invoke(this.target, args);
        }

        public RabbitListener target() {
            return this.target;
        }

        public String admin() {
            return this.admin;
        }
    }
}
