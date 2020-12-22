package com.emily.framework.cloud.autoconfigure.retry;

import com.emily.framework.cloud.autoconfigure.retry.listener.EmilyRetryListener;
import com.emily.framework.common.utils.log.LoggerUtils;
import com.emily.framework.context.httpclient.service.AsyncLogHttpClientService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy;
import org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.BackOffPolicy;

/**
 * @program: spring-parent
 * @description: Ribbon负载均衡重试工厂类
 * @create: 2020/11/13
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate", value = {RibbonLoadBalancedRetryFactory.class})
@ConditionalOnMissingBean(value = RibbonLoadBalancedRetryFactory.class)
@AutoConfigureBefore(RibbonAutoConfiguration.class)
public class EmilyRibbonLoadBalancedRetryFactory extends RibbonLoadBalancedRetryFactory implements InitializingBean, DisposableBean {

    private AsyncLogHttpClientService asyncLogHttpClientService;

    public EmilyRibbonLoadBalancedRetryFactory(SpringClientFactory clientFactory, AsyncLogHttpClientService asyncLogHttpClientService) {
        super(clientFactory);
        this.asyncLogHttpClientService = asyncLogHttpClientService;
    }

    @Override
    public LoadBalancedRetryPolicy createRetryPolicy(String service, ServiceInstanceChooser serviceInstanceChooser) {
        return super.createRetryPolicy(service, serviceInstanceChooser);
    }

    @Override
    public BackOffPolicy createBackOffPolicy(String service) {
        return super.createBackOffPolicy(service);
    }

    @Override
    public RetryListener[] createRetryListeners(String service) {
        RetryListener[] listeners = new RetryListener[]{new EmilyRetryListener(asyncLogHttpClientService)};
        return listeners;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(EmilyRibbonLoadBalancedRetryFactory.class, "【销毁--自动化配置】----Ribbon负载均衡重试工厂组件【EmilyRibbonLoadBalancedRetryFactory】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(EmilyRibbonLoadBalancedRetryFactory.class, "【初始化--自动化配置】----Ribbon负载均衡重试工厂组件【EmilyRibbonLoadBalancedRetryFactory】");
    }
}
