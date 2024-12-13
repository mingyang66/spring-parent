package com.emily.infrastructure.web.filter.filter;

import com.emily.infrastructure.tracing.holder.LocalContextHolder;
import com.emily.infrastructure.tracing.holder.TracingStage;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.util.FilterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;


/**
 * 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link ContentCachingRequestWrapper}
 *
 * @author Emily
 * @since 2020/8/19
 */
public class ContentCachingWrapperFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(ContentCachingWrapperFilter.class);

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        //创建ContentCachingRequestWrapper对象用于缓存请求体
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        if (LOG.isDebugEnabled()) {
            LOG.debug("请求接口缓存拦截器：START============>>{}", FilterUtil.getRequestPath(request));
        }
        //标记阶段标识
        LocalContextHolder.current().setTracingStage(TracingStage.PARAMETER);
        //继续执行过滤器链，并传递包装后的请求对象
        filterChain.doFilter(requestWrapper, response);
        //移除线程上下文数据
        LocalContextHolder.unbind(true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("请求接口缓存拦截器：END<<============{}", FilterUtil.getRequestPath(request));
        }
    }
}
