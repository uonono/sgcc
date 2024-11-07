package com.sgcc.sgcc_mgr_bx.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;
import java.sql.Timestamp;

/**
* @Author: cy
* @Date: 2024/11/7 10:25
* @Description: 这张表应该是在抢修服务的，而不是在报修服务里面进行操作和创建  也需要，是在工单详情获取接单人的到达时间、勘察时间等
*/
@Data
@Table("repair_record")
public class RepairRecord {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 工单ID
     */
    private Long faultOrderId;

    /**
     * 抢修人OpenID
     */
    private String repairUserOpenid;

    /**
     * 接单时间
     */
    private Timestamp receiveTime;

    /**
     * 勘察时间
     */
    private Timestamp surveyTime;

    /**
     * 处理时间
     */
    private Timestamp dealWithTime;

    /**
     * 到达时间
     */
    private Timestamp arriveTime;

}
