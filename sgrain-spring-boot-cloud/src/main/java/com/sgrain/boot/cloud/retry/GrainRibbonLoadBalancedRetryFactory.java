package com.sgrain.boot.cloud.retry;

import com.sgrain.boot.cloud.retry.listener.GrainRetryListener;
import com.sgrain.boot.common.utils.log.LoggerUtils;
import com.sgrain.boot.context.httpclient.service.AsyncLogHttpClientService;
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
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.BackOffPolicy;

/**
 * @program: spring-parent
 * @description: Ribbon负载均衡重试工厂类
 * @create: 2020/11/13
 */
@ConditionalOnClass(name = "org.springframework.retry.support.RetryTemplate", value = {RibbonLoadBalancedRetryFactory.class})
@ConditionalOnMissingBean(value = RibbonLoadBalancedRetryFactory.class)
@AutoConfigureBefore(RibbonAutoConfiguration.class)
public class GrainRibbonLoadBalancedRetryFactory extends RibbonLoadBalancedRetryFactory implements InitializingBean, DisposableBean {

    private AsyncLogHttpClientService asyncLogHttpClientService;

    public GrainRibbonLoadBalancedRetryFactory(SpringClientFactory clientFactory, AsyncLogHttpClientService asyncLogHttpClientService) {
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
        RetryListener[] listeners = new RetryListener[]{new GrainRetryListener(asyncLogHttpClientService)};
        return listeners;
    }

    @Override
    public void destroy() throws Exception {
        LoggerUtils.info(GrainRibbonLoadBalancedRetryFactory.class, "【销毁--自动化配置】----Ribbon负载均衡重试工厂组件【SmallGrainRibbonLoadBalancedRetryFactory】");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerUtils.info(GrainRibbonLoadBalancedRetryFactory.class, "【初始化--自动化配置】----Ribbon负载均衡重试工厂组件【SmallGrainRibbonLoadBalancedRetryFactory】");
    }
}
