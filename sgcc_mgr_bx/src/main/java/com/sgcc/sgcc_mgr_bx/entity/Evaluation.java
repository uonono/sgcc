package com.sgcc.sgcc_mgr_bx.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Table("evaluation")
public class Evaluation {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工单id
     */
    private Long faultOrderId;

    /**
     * 服务态度评分
     * 范围通常为1-5，表示用户对服务态度的评价
     */
    private Integer serviceAttitude;

    /**
     * 抢修及时性评分
     * 范围通常为1-5，表示用户对维修及时性的评价
     */
    private Integer repairTimeliness;

    /**
     * 用户评价内容
     * 用户对维修服务的文字评价
     */
    private String comments;

    /**
     * 评价创建时间
     * 记录评价的创建时间，用于表示评价的时间点
     */
    private Timestamp createdAt;
}
