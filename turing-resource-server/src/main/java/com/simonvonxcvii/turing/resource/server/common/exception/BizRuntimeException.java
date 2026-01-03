package com.simonvonxcvii.turing.resource.server.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 *
 * @author Simon Von
 * @since 7/31/23 5:09 AM
 */
@Getter
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

}
