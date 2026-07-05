package com.simonvonxcvii.turing.common.exception;

/**
 * 业务异常类
 *
 * @author Simon Von
 * @since 7/31/23 5:09 AM
 */
public class BizRuntimeException extends RuntimeException {

    @java.io.Serial
    private static final long serialVersionUID = 1L;
    /**
     * 消息
     */
    private final String message;
    /**
     * 是否错误，只能赋值 null，如果赋 true 前端 alert 无法显示信息
     */
    private final Boolean error;
    /**
     * 响应 Code
     */
    private final Integer code;

    public BizRuntimeException(String message) {
        super(message);
        this.message = message;
        this.error = null;
        this.code = 1;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Boolean getError() {
        return error;
    }

    public Integer getCode() {
        return code;
    }

}
