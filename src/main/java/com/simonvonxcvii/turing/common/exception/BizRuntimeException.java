package com.simonvonxcvii.turing.common.exception;

import com.simonvonxcvii.turing.common.result.ResultCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 业务异常类
 */
@Getter
public class BizRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 异常码
     */
    private final String code;

    /**
     * 响应数据
     */
    private final Object result;

    public BizRuntimeException(String message) {
        this(ResultCode.ERROR.code(), message);
    }

    public BizRuntimeException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public BizRuntimeException(String errorCode, String message, Object result) {
        super(message);
        this.code = errorCode;
        this.result = result;
    }

    public BizRuntimeException(Throwable cause) {
        super(cause);
        code = ResultCode.ERROR.code();
        result = null;
    }

    public static BizRuntimeException from(String message) {
        return from(ResultCode.ERROR, message, null);
    }

    public static BizRuntimeException from(ResultCode code) {
        return from(code, code.message(), null);
    }

    public static BizRuntimeException from(ResultCode code, Object result) {
        return from(code, code.message(), result);
    }

    public static BizRuntimeException from(ResultCode code, String message) {
        return from(code, message, null);
    }

    public static BizRuntimeException from(ResultCode code, String message, Object result) {
        return new BizRuntimeException(code.code(), message != null ? message : code.message(), result);
    }

    public Object getData() {
        return result;
    }

}
