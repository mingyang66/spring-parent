package com.yaomy.security.jwt.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: RBAC基于角色的权限访问控制
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.service.RbacAuthorityService
 * @Author: 姚明洋
 * @Date: 2019/7/1 17:12
 * @Version: 1.0
 */
public interface RbacService {
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
