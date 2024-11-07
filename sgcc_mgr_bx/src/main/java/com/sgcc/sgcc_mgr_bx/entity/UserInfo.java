package com.sgcc.sgcc_mgr_bx.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
@Data
@Table("user_info")
public class UserInfo {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 微信unionid
     */
    private String unionid;

    /**
     * 头像
     */
    private String headimgurl;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 地址
     */
    private String address;

    /**
     * 户号
     */
    private String accountNumber;



}
