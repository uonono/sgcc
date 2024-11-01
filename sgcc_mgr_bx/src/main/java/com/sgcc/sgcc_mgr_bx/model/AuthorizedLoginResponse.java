package com.sgcc.sgcc_mgr_bx.model;

import lombok.Data;

import java.util.List;

/**
* @Author: cy
* @Date: 2024/10/17 14:26
* @Description: 登录传输类
*/
@Data
public class AuthorizedLoginResponse {

    // Getter 和 Setter 方法
    private String openid;
    private String unionid;
    private String accessToken;

}
