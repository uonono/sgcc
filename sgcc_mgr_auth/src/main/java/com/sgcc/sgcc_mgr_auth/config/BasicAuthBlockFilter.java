package com.sgcc.sgcc_mgr_auth.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
/** 
* @Author: cy
* @Date: 2024/10/9 14:14
* @Description: 进行对非token请求的JWT强制认证
*/
@Component
public class BasicAuthBlockFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 检查是否有 Authorization 头并且是 Basic 认证
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            // 如果请求路径不是 /token，则阻止访问
            String path = exchange.getRequest().getPath().toString();
            if (!"/token".equals(path)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // 拒绝请求
            }
        }
        return chain.filter(exchange);
    }
}
