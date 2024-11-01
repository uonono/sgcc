package com.sgcc.sgcc_mgr_auth.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
/**
* @Author: cy
* @Date: 2024/8/2 上午10:42
* @Description: 创建一个新的类，实现Converter<Jwt, Collection<GrantedAuthority>>接口，并在其中包含自定义的转换和验证逻辑。
*/
public class CustomGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    /**
     * 这个方法的作用是将jwt实体类转换为对应的controller的
     * @param jwt
     * @return
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);
        
        // 在这里插入您的自定义验证逻辑，例如检查Token是否在黑名单中
        if (isTokenBlacklisted(jwt)) {
            throw new JwtException("Token is blacklisted");
        }

        return authorities;
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
