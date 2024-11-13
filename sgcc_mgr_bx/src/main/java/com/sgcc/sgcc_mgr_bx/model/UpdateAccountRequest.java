package com.sgcc.sgcc_mgr_bx.model;

import lombok.Data;

/**
 * 更新账户请求模型类，用于接收更新账户时的请求数据。
 */
@Data
public class UpdateAccountRequest {

    /**
     * 主键id，用于标识账户的唯一性。
     */
    private Long id;

    /**
     * 账户地址，表示用户的主要地址。
     */
    private String address;

    /**
     * 账户详细地址，表示用户地址的补充信息。
     */
    private String detailAddress;

    /**
     * 账户账号，通常是用户登录或识别的唯一标识符。
     */
    private String account;

    /**
     * 标签ID，表示账户的标签或者分类ID。
     */
    private Long tagId;

    /**
     * 纬度，表示用户位置的纬度坐标。
     */
    private Double latitude;

    /**
     * 经度，表示用户位置的经度坐标。
     */
    private Double longitude;
}
