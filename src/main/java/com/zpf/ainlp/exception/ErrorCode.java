package com.zpf.ainlp.exception;

import java.text.MessageFormat;

public enum ErrorCode {
    SUCCESS(200, "success"),
    /**
     * 系统异常 1000-1999
     **/
    METHOD_ERROR(405, "请求方法异常"),
    SERVER_ERROR(500, "服务器飞走了......"),
    ACCOUNT_LOCKED(999, "账户已锁定，30分钟后重试"),
    FORBIDDEN(1000, "无权访问，请登录后重试"),

    REQUEST_PARAMS_ERROR(1001, "请求参数错误,请重试"),
    PARAM_INCOMPLETE(1002, "参数不完整"),
    PARAM_ILLEGAL(1003, "参数不合法"),
    HTTP_REQUEST_EXCEPTION(1004, "http请求第三方接口异常"),
    NO_RESULT(1005, "无匹配数据"),
    REQUEST_TIMEOUT(1006, "请求超时"),
    REQUEST_SIGN_ERROR(1007, "校验失败,请重试"),
    NOT_RESUBMIT(1009, "请勿重复提交"),

    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorCode getByCode(int code) {
        ErrorCode[] values = ErrorCode.values();
        for (ErrorCode value : values) {
            if (value.getCode() == code)
                return value;
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getMessage(Object... param) {
        if (param.length == 0) {
            return message;
        }
        return MessageFormat.format(message, param);
    }

}
