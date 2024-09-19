package com.simonvonxcvii.turing.common.result;

public interface ResultCode {

    /**
     * 操作成功
     */
    ResultCode SUCCESS = new DefaultResultCode("SUCCESS", "操作成功", true);

    /**
     * 操作失败
     */
    ResultCode ERROR = new DefaultResultCode("ERROR", "操作失败", false);

    /**
     * 请求错误
     */
    ResultCode BAD_REQUEST = new DefaultResultCode("BAD_REQUEST", "请求错误", false);

    /**
     * 资源未找到
     */
    ResultCode NOT_FOUND = new DefaultResultCode("NOT_FOUND", "请求数据未找到", false);

    /**
     * 生成一个 {@link ResultCode} 实例
     */
    static DefaultResultCode error(String code, String message) {
        return new DefaultResultCode(code, message, false);
    }

    /**
     * 生成一个 {@link ResultCode} 实例
     */
    static ResultCode ok(String code, String message) {
        return new DefaultResultCode(code, message, true);
    }

    /**
     * 响应结果 Code
     */
    String code();

    /**
     * 响应消息
     */
    String message();

    /**
     * 是否成功
     */
    Boolean isSuccess();
}
