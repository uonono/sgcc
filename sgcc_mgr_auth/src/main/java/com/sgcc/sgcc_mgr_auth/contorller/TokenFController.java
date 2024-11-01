package com.sgcc.sgcc_mgr_auth.contorller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class TokenFController {

    // 创建 WebClient 实例，直接在控制器中使用
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:9000") // 设置基础 URL
            .build();

    @GetMapping("/get-token")
    public Mono<String> getToken() {
        // 使用 WebClient 发送 POST 请求到 /token
        return webClient.post()
                .uri("/token")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class) // 将响应体转换为字符串
                .onErrorResume(e -> {
                    // 错误处理
                    return Mono.just("Error occurred: " + e.getMessage());
                });
    }
}
