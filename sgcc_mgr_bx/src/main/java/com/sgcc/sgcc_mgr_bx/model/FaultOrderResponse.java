package com.sgcc.sgcc_mgr_bx.model;

import com.sgcc.sgcc_mgr_bx.entity.FaultOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @Author: cy
* @Date: 2024/11/19 10:10
* @Description: 设计表添加字段应该是用既定事实决定状态，而不是状态关联既定事实  ，procCode应该是经过逻辑计算的，不然数据库删除之后会出现逻辑错误
*/
@EqualsAndHashCode(callSuper = true)
@Data
public class FaultOrderResponse extends FaultOrder {

    /**
     * 昵称
     */
    private String workerName;

    /**
     * 联系电话
     */
    private String workerPhone;

    /**
     * 当前工单状态，0-8的个位数字
     */
    private Integer procCode;

}
