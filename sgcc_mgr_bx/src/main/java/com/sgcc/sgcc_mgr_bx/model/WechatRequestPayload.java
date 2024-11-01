package com.sgcc.sgcc_mgr_bx.model;

public class WechatRequestPayload {

    private String describtion;
    private String DocumentElement;
    private String Tokencode;
    private String hisType;
    private String hospitalId;

    // Getters and Setters
    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getDocumentElement() {
        return DocumentElement;
    }

    public void setDocumentElement(String documentElement) {
        DocumentElement = documentElement;
    }

    public String getTokencode() {
        return Tokencode;
    }

    public void setTokencode(String tokencode) {
        Tokencode = tokencode;
    }

    public String getHisType() {
        return hisType;
    }

    public void setHisType(String hisType) {
        this.hisType = hisType;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
}
