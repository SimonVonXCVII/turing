package com.simonvonxcvii.turing.common.result;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

// TODO: 2023/4/12 是否能够改用为 ResponseEntity
//  例如：Kotlin 的 kotlin.Result
//  fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T>
//  fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T>

/**
 * 标准的 Restful 请求响应实体。
 */
@Getter
@ToString
public class Result<T> implements Serializable {

    /**
     * 响应 Code
     */
    private final Integer code;

    /**
     * 响应数据
     */
    private final T data;

    /**
     * 是否成功
     */
    private final String error;

    /**
     * 消息
     */
    private final String message;

    protected Result(Integer code, T data, String error, String message) {
        this.code = code;
        this.data = data;
        this.error = error;
        this.message = message;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, data, null, "ok");
    }

    public static <T> Result<T> ok() {
        return new Result<>(0, null, null, "ok");
    }

    public static <T> Result<T> error(String error) {
        return new Result<>(1, null, error, "error");
    }
}
