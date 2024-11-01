package com.sgcc.sgcc_mgr_gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CustomResponseGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();

        // 移除所有可能的旧的 Vary 和 Access-Control-Allow-Origin 头部
        headers.remove("Vary");
        headers.remove("Access-Control-Allow-Origin");

        // 打印所有响应头（可选）
        System.out.println("===== Response Headers Before =====");
        headers.forEach((key, values) -> System.out.println(key + " : " + values));
        System.out.println("===========================");

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 删除重复的 Vary 头，只保留一个
            headers.set("Vary", "Origin, Access-Control-Request-Method, Access-Control-Request-Headers");

            // 确保 Access-Control-Allow-Origin 只有一个值
            headers.set("Access-Control-Allow-Origin", "*");

            // 移除不需要的响应头
            headers.remove("X-Frame-Options");
            headers.remove("X-Xss-Protection");
            headers.remove("X-Content-Type-Options");

            // 确保 HTTP/3 (QUIC) 支持的 Alt-Svc 头部是唯一的
            headers.set("Alt-Svc", "h3=\":9999\"; ma=86400");

            // 打印处理后的响应头（可选）
            System.out.println("===== Response Headers After =====");
            headers.forEach((key, values) -> System.out.println(key + " : " + values));
            System.out.println("===========================");
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE; // 确保在最后执行
    }
}
