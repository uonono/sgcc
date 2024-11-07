package com.sgcc.sgcc_mgr_bx.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("account_table")
public class Account {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 户号
     */
    private Long id;

    /**
     * 地址
     */
    private String address;

    /**
     * 地址
     */
    private String account;

    /**
     * 详情地址
     */
    private String detailAddress;

    /**
     * 标签ID
     */
    private Long tagId;


}
