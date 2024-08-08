package com.emily.infrastructure.autoconfigure.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.web.context.support.ServletRequestHandledEvent;

/**
 * ---------------------------------------------------------------------
 * ServletRequestHandledEvent是每当Spring MVC应用程序处理HTTP请求时由Spring Framework触发的事件。
 * 此事件提供有关已处理的请求的信息。例如：请求URL、HTTP方法和请求处理时间。
 * <p>
 * 此事件的目的是允许开发人员监视和分析其SpringMVC应用程序的性能。通过监听此事件，开发人员可以跟踪请求处理时间、
 * 处理的请求数和错误率等指标。这些信息可用于识别性能瓶颈、优化资源利用率以及改善应用程序的整体用户体验。
 * <p>
 * 此外，ServletRequestHandledEvent可用于实现自定义日志记录，审计和安全措施。例如，开发人员可能会使用此事件来
 * 记录用用程序处理的所有请求，或者根据发出请求的用户实施访问控制。
 * <p>
 * 总的来说，ServletRequestHandledEvent是一个强大的工具，用于监视和分析SpringMVC应用程序的行为，以及实现基于HTTP
 * 请求处理的自定义功能。
 * ---------------------------------------------------------------------
 * <p>
 * ServletRequestHandledEvent事件监听器
 *
 * @author Emily
 * @since Created in 2023/6/24 7:28 PM
 */
public class ServletRequestHandledApplicationListener implements ApplicationListener<ServletRequestHandledEvent> {
    @Override
    public void onApplicationEvent(ServletRequestHandledEvent event) {
        //todo 记录请求日志、监控、安全校验
    }
}
