package com.simonvonxcvii.turing.common.result;

/**
 * 标准的 Restful 请求响应实体。
 *
 * @param <T>     响应数据
 * @param code    响应 Code
 * @param data    响应数据
 * @param error   是否错误，只能赋值 null，如果赋 true 前端 alert 无法显示信息
 * @param message 消息
 * @author Simon Von
 * @since 7/31/23 5:09 AM
 */
public record Result<T>(Integer code, T data, Boolean error, String message) {

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, data, null, "ok");
    }

    public static <T> Result<T> ok() {
        return new Result<>(0, null, null, "ok");
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(1, null, null, message);
    }

}
