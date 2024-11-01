package com.sgcc.sgcc_mgr_bx.config;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class Http3AltSvcFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 添加自定义 HTTP/3 Alt-Svc 头部
        exchange.getResponse().getHeaders().add("Alt-Svc", "h3=\":9999\"; ma=86400");

        // 添加 CORS 响应头，允许跨域
//        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
//        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
//        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");

        return chain.filter(exchange);
    }
}
