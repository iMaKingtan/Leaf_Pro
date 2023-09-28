package com.generate.common.base;

/**
 * @DESCRIPTION: 结果状态
 * @CREATED by: iMaKingtan
 * @DATE: 2022/5/30 21:45
 */
public enum ResultState {

    SUCCESS(200, "请求成功"),
    FAILED(500, "服务器内部异常");

    private final int code;
    private final String msg;

    ResultState(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
