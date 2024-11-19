package com.sgcc.sgcc_mgr_qx.model;

import lombok.Data;

import java.util.List;

@Data
public class UserInfoResponse {

    private String phone = "13711065366";
    private String nickname = "灬辰";
    private int sex = 1;
    private String province;
    private String session_key;
    private String city;
    private String country;
    private String headimgurl = "https://thirdwx.qlogo.cn/mmopen/vi_32/4GniamicH0hOXZWia9VbOQFvYVibHqccLfKDWRDHf3AV8bLcBlbKmRWWg1aSKSFzSL6uIcfibFPuwiaQ9345gvqtO9rA/132";
    private List<String> privilege;
    private String unionid;
    private String language; // 新增的 language 字段

}
