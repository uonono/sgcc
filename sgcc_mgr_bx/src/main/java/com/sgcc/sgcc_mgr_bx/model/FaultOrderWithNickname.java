package com.sgcc.sgcc_mgr_bx.model;

import com.sgcc.sgcc_mgr_bx.entity.FaultOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FaultOrderWithNickname extends FaultOrder {

    /**
     * 昵称
     */
    private String workerName;

    /**
     * 联系电话
     */
    private String workerPhone;
}
