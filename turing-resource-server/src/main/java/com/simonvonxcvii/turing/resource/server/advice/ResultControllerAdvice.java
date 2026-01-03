package com.simonvonxcvii.turing.resource.server.advice;

import com.simonvonxcvii.turing.resource.server.common.exception.BizRuntimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLException;

/**
 * @author Simon Von
 * @since 7/31/23 5:09 AM
 */
@RestControllerAdvice
public class ResultControllerAdvice {

    private static final Log log = LogFactory.getLog(ResultControllerAdvice.class);

    private final Environment environment;

    public ResultControllerAdvice(Environment environment) {
        this.environment = environment;
    }

    /**
     * 业务异常类
     */
    @ExceptionHandler(BizRuntimeException.class)
    public ResponseEntity<BizRuntimeException> bizRuntimeException(BizRuntimeException e) {
        log.error("@ExceptionHandler(BizRuntimeException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    /**
     * 当上传文件大小超过允许的最大上传大小时，会抛出 MultipartException 的子类异常。
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BizRuntimeException> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("@ExceptionHandler(MaxUploadSizeExceededException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException("上传的文件超过限制，当前最大允许: "
                + environment.getProperty("spring.servlet.multipart.max-file-size")));
    }

    /**
     * 当 HttpMessageConverter 实现类的 read 方法失败时，会抛出此异常。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BizRuntimeException> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("@ExceptionHandler(HttpMessageNotReadableException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

    /**
     * BindException: 当绑定错误被视为致命错误时抛出此异常。它实现了 BindingResult 接口（及其父接口 Errors），以便直接分析绑定错误。
     * 从 Spring 2.0 版本开始，这是一个特殊用途的类。通常，应用程序代码会使用 BindingResult 接口，或者使用 DataBinder，
     * 而 DataBinder 又会通过 DataBinder.getBindingResult() 方法暴露 BindingResult 接口。
     * MethodArgumentNotValidException: 当使用 @Valid 或 @Validated 注解的参数验证失败时，会抛出 BindException 异常。
     * MissingServletRequestParameterException: ServletRequestBindingException 子类，指示缺少参数。
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<BizRuntimeException> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("@ExceptionHandler(MethodArgumentNotValidException.class): ", e);
        if (e.hasFieldErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getFieldErrors().getFirst().getDefaultMessage()));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

    /**
     * 当指定的 SQL 语句无效时，会抛出此异常。此类异常的根本原因始终是 java.sql.SQLException。
     * 可以创建针对“表不存在”、“列不存在”等情况的子类。自定义的 SQLExceptionTranslator 可以创建此类更具体的异常，而不会影响使用此类的代码。
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<BizRuntimeException> badSqlGrammarException(BadSqlGrammarException e) {
        log.error("@ExceptionHandler(BindException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

    /**
     * 此异常提供有关数据库访问错误或其他错误的信息。
     */
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<BizRuntimeException> sQLException(SQLException e) {
        log.error("@ExceptionHandler(SQLException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

    /**
     * 此异常用于指示方法接收到了非法或不合适的参数。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BizRuntimeException> illegalArgumentException(IllegalArgumentException e) {
        log.error("@ExceptionHandler(IllegalArgumentException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<BizRuntimeException> nullPointerException(NullPointerException e) {
        log.error("@ExceptionHandler(NullPointerException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException("目标对象或值为空: " + e.getLocalizedMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BizRuntimeException> runtimeException(RuntimeException e) {
        log.error("@ExceptionHandler(RuntimeException.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BizRuntimeException> exception(Exception e) {
        log.error("@ExceptionHandler(Exception.class): ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BizRuntimeException(e.getLocalizedMessage()));
    }

}
