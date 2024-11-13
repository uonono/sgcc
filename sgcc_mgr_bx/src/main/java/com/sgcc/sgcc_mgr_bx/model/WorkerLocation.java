package com.sgcc.sgcc_mgr_bx.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @Author: cy
* @Date: 2024/11/13 14:27
* @Description: 抢修人员位置表  存于redis
*/
@NoArgsConstructor
@Data
public class WorkerLocation {

    /**
     * 抢修人员维度
     */
    private Double latitude;

    /**
     * 抢修人员经度
     */
    private Double longitude;

    // 构造方法
    public WorkerLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
