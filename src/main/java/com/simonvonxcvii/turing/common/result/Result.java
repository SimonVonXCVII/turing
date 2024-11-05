package com.simonvonxcvii.turing.common.result;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

// TODO: 2023/4/12 是否能够改用为 ResponseEntity

/**
 * 标准的 Restful 请求响应实体。
 */
@Getter
public class Result<T> implements Serializable {

    /**
     * 响应 Code
     */
    private final String code;

    /**
     * 是否成功
     */
    private final Boolean success;

    /**
     * 消息
     */
    private final String message;

    /**
     * 响应数据
     */
    private final T result;

    protected Result(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
        this.success = Objects.equals(ResultCode.SUCCESS.code(), code);
    }

    protected Result(ResultCode resultCode, T result) {
        this(resultCode.code(), resultCode.message(), result);
    }

    protected Result(ResultCode resultCode, String message) {
        this(resultCode.code(), message, null);
    }

    public static <T> Result<T> ok(T result) {
        return new Result<>(ResultCode.SUCCESS, result);
    }

    public static <T> Result<T> ok() {
        return new Result<>(ResultCode.SUCCESS, (T) null);
    }

    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.code(), "操作失败", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR, message);
    }

    public static <T> Result<T> error(String code, String message, T result) {
        return new Result<>(code, message, result);
    }

    public static <T> Result<T> error(String code, String message) {
        return new Result<>(code, message, null);
    }

    public Object getData() {
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code='" + code + '\'' +
                ", success=" + success +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
