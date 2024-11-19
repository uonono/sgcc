package com.sgcc.sgcc_mgr_qx.controllor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgcc.sgcc_mgr_qx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_qx.model.LoginResponse;
import com.sgcc.sgcc_mgr_qx.model.UserInfoResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
* @Author: cy
* @Date: 2024/11/18 09:14
* @Description: 抢修端登录接口
*/
@RestController
public class AuthController {

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public AuthController(WebClient webClient, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.webClient = webClient.mutate()
                .baseUrl("https://127.0.0.1:9000")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /*@GetMapping("/func/Common/loginByRider")
    public Mono<AjaxResponse> loginByRider(@RequestParam String code) {
        // 调用内部方法获取 openid
        String openid = getOpenIdByCode(code);

        // 使用 WebClient 调用 /token 接口
        return webClient.get()
                .uri("/token")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader(openid, openid))
                .retrieve()
                .bodyToMono(String.class)
                .map(token -> AjaxResponse.success(new LoginResponse(openid, token)))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("Failed to fetch token: " + e.getMessage())));
    }*/

    /**
     * 登录测试接口  传入为空
     * @return token加openid
     */
    @PostMapping("/authLogin")
    public Mono<AjaxResponse> loginByRider() {
        // 调用内部方法获取 openid
        String openid = getOpenIdByCode("code");
        // 使用 WebClient 调用 /token 接口
        return webClient.post()
                .uri("/token")
                .header(HttpHeaders.AUTHORIZATION, createBasicAuthHeader(openid, openid))
                .retrieve()
                .bodyToMono(String.class)
                .map(token -> AjaxResponse.success(new LoginResponse(token,new UserInfoResponse())))
                .onErrorResume(e -> Mono.just(AjaxResponse.error("Failed to fetch token: " + e.getMessage())));
    }

    // 内部方法，根据 code 获取 openid
    private String getOpenIdByCode(String code) {
        // 模拟根据 code 返回 openid 的逻辑
//        return code;
        return "o7uzT6m8cMXwh03rQ0egT8-Snd9E";
    }

    // 创建 Basic Auth Header
    private String createBasicAuthHeader(String username, String password) {
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }
}
