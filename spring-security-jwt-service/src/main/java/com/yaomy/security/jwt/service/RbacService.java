package com.yaomy.security.jwt.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: RBAC基于角色的权限访问控制
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public interface RbacService {
    boolean hasPermission(HttpServletRequest request, Authentication authentication);
}
