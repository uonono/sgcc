package com.sgcc.sgcc_mgr_auth.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import reactor.core.publisher.Flux;

import java.util.Collection;
/**
* @Author: cy
* @Date: 2024/10/8 15:58
* @Description: webflux的jwt黑名单判断
*/
public class CustomReactiveGrantedAuthoritiesConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        // 假设从 JWT 的 claims 中提取 "roles"
        Collection<String> roles = jwt.getClaim("roles");

        if (isTokenBlacklisted(jwt)) {
            throw new JwtException("Token is blacklisted");
        }

        // 如果 roles 是 null，返回一个空的 Flux
        if (roles == null) {
            return Flux.empty();
        }

        // 将 roles 转换为 Flux<GrantedAuthority>
        return Flux.fromIterable(roles)
            .map(SimpleGrantedAuthority::new); // 将每个角色转换为 GrantedAuthority
    }

    /**
     * 从这里进行判断对应的逻辑实现，包括Redis中每个用户的5个token策略，拉黑（先进先出）等策略的放行实现
     * @param jwt 这个应该是spring中从request头中获取的token，（也可能包含签发时间、过期时间等）
     * @return 当前token是否通过对应的逻辑
     */
    private boolean isTokenBlacklisted(Jwt jwt) {
        // 实现Token黑名单检查逻辑
        // 这里是一个示例，返回false表示Token不在黑名单中
        return false;
    }

}
