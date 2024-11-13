package com.sgcc.sgcc_mgr_bx.model;

import lombok.Data;

/**
 * 创建标签请求模型类，用于接收创建标签时的请求数据。
 */
@Data
public class TagCreateRequest {

    /**
     * 标签名称
     */
    private String tagName;

}
