package com.sgcc.sgcc_mgr_qx.model;

import lombok.Data;

/**
* @Author: cy
* @Date: 2024/11/11 17:01
* @Description: 公共的int的Mono返回，用于无法int返回的SQL接口语句
*/
@Data
public class StatusModel {

    /**
     * 状态字段，0 表示否，1 表示是
     */
    private int status;

    /**
     * 判断状态是否为 1
     * @return 如果状态为 1，返回 true，否则返回 false
     */
    public boolean isStatusOne() {
        return this.status == 1;
    }
}
