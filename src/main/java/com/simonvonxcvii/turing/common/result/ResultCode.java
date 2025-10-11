//package com.simonvonxcvii.turing.common.result;
//
//public class ResultCode {
//    /**
//     * 操作成功
//     */
//    public static ResultCode SUCCESS = new ResultCode(0, "ok", null);
//    /**
//     * 操作失败
//     */
//    public static ResultCode ERROR = new ResultCode(1, "error", null);
//    /**
//     * 请求错误
//     */
//    public static ResultCode BAD_REQUEST = new ResultCode(2, "bad request", null);
//    /**
//     * 资源未找到
//     */
//    public static ResultCode NOT_FOUND = new ResultCode(3, "not found", null);
//
//    public ResultCode() {
//    }
//
//    public ResultCode(ResultCode SUCCESS, ResultCode ERROR, ResultCode BAD_REQUEST, ResultCode NOT_FOUND) {
//        this.SUCCESS = SUCCESS;
//        this.ERROR = ERROR;
//        this.BAD_REQUEST = BAD_REQUEST;
//        this.NOT_FOUND = NOT_FOUND;
//    }
//
//    /**
//     * 生成一个 {@link ResultCode} 实例
//     */
//    static ResultCode ok(Integer code, String message, String error) {
//        return new DefaultResultCode(code, message, error);
//    }
//
//    /**
//     * 响应结果 Code
//     */
//    Integer code();
//
//    /**
//     * 响应消息
//     */
//    String message();
//
//    /**
//     * 错误消息
//     */
//    String error();
//}
