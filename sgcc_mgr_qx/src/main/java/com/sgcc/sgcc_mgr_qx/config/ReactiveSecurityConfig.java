package com.sgcc.sgcc_mgr_qx.config;

import com.sgcc.sgcc_mgr_qx.security.CustomReactiveAuthorizationManager;
import com.sgcc.sgcc_mgr_qx.security.CustomServerAccessDeniedHandler;
import com.sgcc.sgcc_mgr_qx.security.CustomServerAuthenticationEntryPoint;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * <h1>安全认证配置</h1>
 * Created by hanqf on 2020/11/19 10:26.
 */

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity //启用@PreAuthorize注解配置
public class ReactiveSecurityConfig {

    @Autowired
    private CustomServerAccessDeniedHandler customServerAccessDeniedHandler;

    @Autowired
    private CustomServerAuthenticationEntryPoint customServerAuthenticationEntryPoint;

    @Autowired
    private CustomReactiveAuthorizationManager customReactiveAuthorizationManager;

    /**
     * 自定义信任库 暂未实验
     * @return 成功加载信任库的webClient
     * @throws Exception 还是推荐去搞公网ip帮域名 然后CA证书
     */
    @Bean
    public HttpClient customHttpClient() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        Resource resource = new ClassPathResource("mykeystore.jks");
        try (InputStream keyStoreStream = resource.getInputStream()) {
            keyStore.load(keyStoreStream, "changeit".toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(tmf)
                .build();

        return HttpClient.create().secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
    }

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/res/**", "/userInfo/**").authenticated() // 需要认证
                .pathMatchers("/user/**").hasAnyRole("admin", "user")   // 特定角色访问
                .pathMatchers("/swagger-doc/**", "/v3/api-docs/**","/authLogin").permitAll() // 公开的API文档和登录接口
                .anyExchange().access(customReactiveAuthorizationManager)) // 自定义认证管理
            .csrf(ServerHttpSecurity.CsrfSpec::disable) // 关闭CSRF
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // 禁用HTTP Basic认证
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 禁用表单登录
            .cors(Customizer.withDefaults()) // 开启CORS支持
            .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(jwt -> {
                    Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
                    Object authoritiesClaim = jwt.getClaims().get("authorities");
                    if (authoritiesClaim instanceof Collection<?>) {
                        authorities = ((Collection<?>) authoritiesClaim).stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toSet());
                    }

                    Collection<SimpleGrantedAuthority> scopes = new HashSet<>();
                    Object scopesClaim = jwt.getClaims().get("scope");
                    if (scopesClaim instanceof Collection<?>) {
                        scopes = ((Collection<?>) scopesClaim).stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                            .collect(Collectors.toSet());
                    }

                    authorities.addAll(scopes);
                    return Mono.just(new JwtAuthenticationToken(jwt, authorities));
                }))
                .accessDeniedHandler(customServerAccessDeniedHandler)
                .authenticationEntryPoint(customServerAuthenticationEntryPoint))
            .build();
    }

    /**
     * 配置CORS规则
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 允许所有来源，生产环境建议限制为具体域名
        config.addAllowedMethod("*");        // 允许所有HTTP方法
        config.addAllowedHeader("*");        // 允许所有请求头
        config.setAllowCredentials(true);    // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 应用于所有路径
        return source;
    }
}
