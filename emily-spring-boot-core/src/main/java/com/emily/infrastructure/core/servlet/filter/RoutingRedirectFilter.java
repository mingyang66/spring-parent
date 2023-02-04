package com.emily.infrastructure.core.servlet.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Description :  路由重定向
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 5:01 下午
 */
public class RoutingRedirectFilter implements Filter {

    private RoutingRedirectCustomizer routingRedirectCustomizer;

    public RoutingRedirectFilter(RoutingRedirectCustomizer routingRedirectCustomizer) {
        this.routingRedirectCustomizer = routingRedirectCustomizer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String lookupPath = ((HttpServletRequest) request).getRequestURI();
        if (this.routingRedirectCustomizer.containsLookupPath(lookupPath)) {
            String newUrl = this.routingRedirectCustomizer.resolveSpecifiedLookupPath(lookupPath);
            request.getRequestDispatcher(newUrl).forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
