package com.sgcc.sgcc_mgr_bx.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("fault_order")
public class FaultOrder {

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 主键 ID
     */
    private Long id;

    /**
     * 区域名称
     */
    private String addressAreaName;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 户号
     */
    private String account;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 故障描述
     */
    private String content;

    /**
     * 预约时间
     */
    private String orderTime;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 当前工单状态，0-4的个位数字
     */
    private Integer procCode;
}
