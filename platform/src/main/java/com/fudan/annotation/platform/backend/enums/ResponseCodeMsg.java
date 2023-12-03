package com.fudan.annotation.platform.backend.enums;

public enum ResponseCodeMsg {
    /**
     * 成功默认返回
     */
    SUCCESS(200, "success"),
    /**
     * 错误默认返回
     */
    ERROR(5000, "error"),
    /**
     * token 为 null
     */
    TOKEN_NULL(5001, "userToken is null");
    private Integer code;
    private String msg;

    ResponseCodeMsg() {
    }

    ResponseCodeMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
