package com.sgcc.sgcc_mgr_qx.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
* @Author: cy
* @Date: 2024/11/18 09:22
* @Description: 定义返回结果类
*/
@Data
@AllArgsConstructor
public class LoginResponse {

    /**
     * token
     */
    private final String token;

    /**
     * 实体类（用户信息）
     */
    private final UserInfoResponse userInfoResponse;
}