package com.sgcc.sgcc_mgr_bx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AccessTokenResponse {

    // Getter 和 Setter 方法
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private int expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    private String openid;

    private String scope;

    @JsonProperty("is_snapshotuser")
    private int isSnapshotuser;

    private String unionid;

    // 构造方法
    public AccessTokenResponse() {}

    public AccessTokenResponse(String accessToken, int expiresIn, String refreshToken, String openid, String scope, int isSnapshotuser, String unionid) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.openid = openid;
        this.scope = scope;
        this.isSnapshotuser = isSnapshotuser;
        this.unionid = unionid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setIsSnapshotuser(int isSnapshotuser) {
        this.isSnapshotuser = isSnapshotuser;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Override
    public String toString() {
        return "AccessTokenResponse{" +
                "accessToken='" + accessToken + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", openid='" + openid + '\'' +
                ", scope='" + scope + '\'' +
                ", isSnapshotuser=" + isSnapshotuser +
                ", unionid='" + unionid + '\'' +
                '}';
    }
}
