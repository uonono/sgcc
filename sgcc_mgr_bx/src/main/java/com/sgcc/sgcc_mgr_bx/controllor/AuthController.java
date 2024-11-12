package com.sgcc.sgcc_mgr_bx.controllor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgcc.sgcc_mgr_bx.exception.AjaxResponse;
import com.sgcc.sgcc_mgr_bx.exception.CustomException;
import com.sgcc.sgcc_mgr_bx.exception.CustomExceptionType;
import com.sgcc.sgcc_mgr_bx.model.AccessTokenResponse;
import com.sgcc.sgcc_mgr_bx.model.AuthorizedLoginResponse;
import com.sgcc.sgcc_mgr_bx.model.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@RestController
public class AuthController {

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    @Value("${wx.appid}")
    private String appid;

    @Value("${wx.secret}")
    private String secret;

    public AuthController(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient.mutate().baseUrl("https://api.weixin.qq.com").build();
        this.objectMapper = objectMapper;
    }


    /**
     * 解析字符串为AccessTokenResponse对象
     * @param responseBody 微信响应
     * @return 响应后的实体
     */
    private Mono<AccessTokenResponse> parseAccessTokenResponse(String responseBody) {
        try {
            // 将响应体解析为AccessTokenResponse
            AccessTokenResponse response = objectMapper.readValue(responseBody, AccessTokenResponse.class);
            return Mono.just(response);
        } catch (IOException e) {
            // 处理JSON解析错误
            System.err.println("Failed to parse access token response: " + e.getMessage());
            return Mono.error(e);
        }
    }

    /**
     * 开放的登录接口
     *
     * @param requestBody 接收的code授权码
     * @return 用户的个人信息
     */
//    @Parameter(name = "requestBody",description = "requestBody",in = ParameterIn.COOKIE,example = "{authCode:authCode}")
    @Operation(summary = "Authentication Login", description = "Authenticates user login with auth code")
    @PostMapping("/authLogin")
    public Mono<AjaxResponse> authLogin(@RequestBody(required = false) Map<String, String> requestBody) {
        if (requestBody == null || !requestBody.containsKey("authCode")) {
            return Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.REQUEST_PARAMETER_ERROR, "Request body is empty or missing code")));
        }
        // 获取 code 参数
        String code = requestBody.get("authCode");

        // 验证 code
        if (code == null || code.matches("^[a-zA-Z]+$") || code.matches("^[0-9]+$")) {
            return Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.REQUEST_PARAMETER_ERROR, "Invalid code format")));
        }
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sns/jscode2session")
                        .queryParam("appid", "wxbc7295dfee15bc75")
                        .queryParam("secret", "54f93b04000bc604c0a9ed57515a9be2")
                        .queryParam("js_code", code)
                        .queryParam("grant_type", "authorization_code")
                        .build())
                .retrieve()
                .bodyToMono(String.class) // 接收响应体为 String
                .flatMap(this::parseUserInfoResponse) // 解析为 UserInfoResponse
                .flatMap(userInfoResponse -> {
                    String openid = userInfoResponse.getOpenid();
                    // 构建 Basic Auth 头，用户名和密码都为 openid
                    String authHeader = "Basic " + Base64.getEncoder()
                            .encodeToString((openid + ":" + openid).getBytes(StandardCharsets.UTF_8));

                    // 发送请求到 127.0.0.1:9000/token
                    return webClient.post()
                            .uri("https://127.0.0.1:9000/token")
                            .header("Authorization", authHeader)
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(token -> {
                                // 构建返回的 AuthorizedLoginResponse 对象
                                AuthorizedLoginResponse authorizedLoginResponse = new AuthorizedLoginResponse();
                                authorizedLoginResponse.setOpenid(openid);
                                authorizedLoginResponse.setAccessToken(token); // 将获取到的 token 存储到 response 中
                                // 将微信用户信息添加到 AuthorizedLoginResponse 对象中
                                System.out.println("Fetched token: " + token);
                                System.out.println("User info response: " + userInfoResponse);
                                return Mono.just(authorizedLoginResponse);
                            });
                })
                .map(AjaxResponse::success)
                .onErrorResume(e -> Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.REQUEST_PARAMETER_ERROR,"请求微信接口失败", e))));
    }

    /**
     * 解析字符串为 UserInfoResponse 对象
     * @param responseBody 微信响应
     * @return 响应后的 UserInfoResponse 实体
     */
    private Mono<UserInfoResponse> parseUserInfoResponse(String responseBody) {
        try {
            // 将响应体解析为 UserInfoResponse
            UserInfoResponse response = objectMapper.readValue(responseBody, UserInfoResponse.class);
            return Mono.just(response);
        } catch (IOException e) {
            // 处理 JSON 解析错误
            System.err.println("Failed to parse user info response: " + e.getMessage());
            return Mono.error(e);
        }
    }
}
