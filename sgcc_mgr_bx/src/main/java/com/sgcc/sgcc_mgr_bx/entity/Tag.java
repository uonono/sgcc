package com.sgcc.sgcc_mgr_bx.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("tag_table")
public class Tag {

    /**
     * 标签ID
     */
    private Long id;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 微信openid
     */
    private String openid;

}
