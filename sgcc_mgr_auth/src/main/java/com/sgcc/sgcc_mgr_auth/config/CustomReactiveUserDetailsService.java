package com.sgcc.sgcc_mgr_auth.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // 可以根据用户名返回一个动态生成的用户
        System.out.println(username + " 这个是用户输入的 username");
        // 模拟根据用户名创建用户（例如自动注册用户）
        UserDetails user = User.withUsername(username)
                .password("{noop}" + username) // 密码这里简单处理，使用用户名作为密码
                .authorities("ROLE_USER") // 默认赋予一个用户角色
                .build();

        // 如果找不到用户，抛出 UsernameNotFoundException 的替代方案是返回 Mono.empty()
        return Mono.just(user);
    }
}
