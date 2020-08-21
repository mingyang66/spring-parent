package com.yaomy.security.jwt.po;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description: 用户认证
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.po.User
 * @Version: 1.0
 */
@Component
public class AuthUserDetailsService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    /**
     * @Description 根据用户名查询用户角色、权限等信息
     * @Version  1.0
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //构建用户信息的逻辑(取数据库/LDAP等用户信息)
        AuthUserDetails userInfo = new AuthUserDetails();
        userInfo.setUsername(username);
        userInfo.setPassword(passwordEncoder.encode("123"));

        Set authoritiesSet = new HashSet();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        authoritiesSet.add(authority);
        //使用逗号分隔符中创建GrantedAuthority数组
        //List<GrantedAuthority> list = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,ROLE_Dev, ROLE_Manager");
        userInfo.setAuthorities(authoritiesSet);

        return userInfo;
    }

}
