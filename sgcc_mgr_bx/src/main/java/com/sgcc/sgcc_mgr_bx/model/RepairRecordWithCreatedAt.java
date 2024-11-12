package com.sgcc.sgcc_mgr_bx.model;

import com.sgcc.sgcc_mgr_bx.entity.RepairRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;
/**
* @Author: cy
* @Date: 2024/11/12 10:04
* @Description: 包含评价的创建时间的Model 返回类
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class RepairRecordWithCreatedAt extends RepairRecord {

    /**
     * 从 evaluation 表获取的 created_at 字段
     */
    private Timestamp createdAt;
}
