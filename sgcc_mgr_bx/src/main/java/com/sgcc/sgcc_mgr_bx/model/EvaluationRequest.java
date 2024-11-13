package com.sgcc.sgcc_mgr_bx.model;

import lombok.Data;

/**
 * 评价请求模型类，用于接收创建评价时的请求数据。
 */
@Data
public class EvaluationRequest {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 态度评分
     */
    private Integer attitudeScore;

    /**
     * 时效评分
     */
    private Integer timelyScore;

    /**
     * 评价内容
     */
    private String content;
}
