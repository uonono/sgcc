package com.sgcc.sgcc_mgr_bx.model;

import com.sgcc.sgcc_mgr_bx.entity.Account;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AccountModel extends Account {
    private String tagName;  // 标签名称
}
