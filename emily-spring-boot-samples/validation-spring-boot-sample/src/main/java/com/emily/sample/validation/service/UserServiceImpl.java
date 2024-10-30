package com.emily.sample.validation.service;

import com.emily.sample.validation.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author :  Emily
 * @since :  2024/10/29 下午3:42
 */
public class UserServiceImpl {
    @NotNull
    @Size(min = 2, max = 20)
    public Set<User> getUsers() {
        Set<User> users = new HashSet<>();
        return users;
    }
}
