package com.sgcc.sgcc_mgr_bx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
* @Author: cy
* @Date: 2024/11/13 14:27
* @Description: 抢修人员位置表  存于redis
*/
@NoArgsConstructor
@Data
@AllArgsConstructor
public class WorkerLocation {

    /**
     * 抢修人员维度
     */
    private Double latitude;

    /**
     * 抢修人员经度
     */
    private Double longitude;

    /**
     * 提交时间
     */
    private Timestamp submitTime;
}
