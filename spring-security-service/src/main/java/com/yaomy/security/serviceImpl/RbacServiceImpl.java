package com.yaomy.security.serviceImpl;

import com.yaomy.security.service.RbacService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description: RBAC基于角色的访问控制
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.service.RbacAuthorityService
 * @Author: 姚明洋
 * @Date: 2019/7/1 16:45
 * @Version: 1.0
 */
@Component(value = "rbacServiceImpl")
public class RbacServiceImpl implements RbacService {

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        System.out.println(authentication.getDetails());
        Object userInfo = authentication.getPrincipal();

        boolean hasPermission  = false;

        if (userInfo instanceof UserDetails) {

            String username = ((UserDetails) userInfo).getUsername();

            //获取资源
            Set<String> urls = new HashSet();
            // 这些 url 都是要登录后才能访问，且其他的 url 都不能访问！
            urls.add("/auth_user/**");
            Set set2 = new HashSet();
            Set set3 = new HashSet();

            AntPathMatcher antPathMatcher = new AntPathMatcher();

            for (String url : urls) {
                if (antPathMatcher.match(url, request.getRequestURI())) {
                    hasPermission = true;
                    break;
                }
            }

            return hasPermission;
        } else {
            return false;
        }
    }
}
