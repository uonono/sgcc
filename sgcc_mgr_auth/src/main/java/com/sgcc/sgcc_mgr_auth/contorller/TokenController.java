package com.sgcc.sgcc_mgr_auth.contorller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RestController
public class TokenController {

    private final JwtEncoder jwtEncoder;

    public TokenController(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    @GetMapping("/me")
    public Mono<String> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> {
                    Authentication authentication = context.getAuthentication();
                    return "Authenticated user: " + authentication.getName();
                });
    }

    @PostMapping("/token")
    public Mono<String> token() {
        return ReactiveSecurityContextHolder.getContext().handle((context, sink) -> {
            Authentication auth = context.getAuthentication();
            Instant now = Instant.now();
            long expiry = 36000L;

            System.out.println("Authentication Information:");
            System.out.println("Name: " + auth.getName());
            System.out.println("Authorities: " + auth.getAuthorities());
            System.out.println("Credentials: " + auth.getCredentials());
            System.out.println("Details: " + auth.getDetails());
            System.out.println("Principal: " + auth.getPrincipal());
            System.out.println("Class: " + auth.getClass().getSimpleName());

            // 区分不同的认证类型
            if (auth instanceof JwtAuthenticationToken) {
                // 如果是 JWT 认证，则返回现有的 token，或者选择进行处理
                System.out.println("JWT Authentication detected.");
                Jwt jwt = ((JwtAuthenticationToken) auth).getToken();
                System.out.println("Existing JWT Token: " + jwt.getTokenValue());
                sink.next(jwt.getTokenValue()); // 直接返回现有的 JWT token
            } else if (auth instanceof UsernamePasswordAuthenticationToken) {
                // 如果是 Basic 认证，则生成新的 JWT token  TODO basic认证也不是生成新的jwt，当账号密码相同时，也是返回原来的（如果没过期）
                System.out.println("Basic Authentication detected.");
                // 获取权限
                String scope = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(" "));
                // 生成 JWT claims
                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer("self")
                        .issuedAt(now)
                        .expiresAt(now.plus(expiry, ChronoUnit.SECONDS))
                        .subject(auth.getName())  // 用户名作为主体
                        .claim("scope", scope)    // 用户权限作为 scope
                        .build();
                // 编码并生成 JWT token
                String newToken = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                System.out.println("Generated new JWT Token: " + newToken);
                sink.next(newToken);
            } else {
                // 如果是其他认证方式，可以根据需求进行处理
                sink.error(new IllegalStateException("Unsupported authentication type: " + auth.getClass().getSimpleName()));
            }
        });
    }
}
