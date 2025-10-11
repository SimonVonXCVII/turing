package com.simonvonxcvii.turing.advice;

import com.simonvonxcvii.turing.common.result.Result;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ResultControllerAdvice {

    private static final Log log = LogFactory.getLog(ResultControllerAdvice.class);

    private final Environment environment;

    public ResultControllerAdvice(Environment environment) {
        this.environment = environment;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Object>> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("@ExceptionHandler(MaxUploadSizeExceededException.class): ", e);
        return ResponseEntity.ok(Result.error("上传的文件超过限制，当前最大允许: " + environment.getProperty("spring.servlet.multipart.max-file-size")));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Object>> illegalArgumentException(IllegalArgumentException e) {
        log.error("@ExceptionHandler(IllegalArgumentException.class): ", e);
        return ResponseEntity.ok(Result.error(e.getLocalizedMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result<Object>> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("@ExceptionHandler(HttpMessageNotReadableException.class): ", e);
        return ResponseEntity.ok(Result.error(e.getLocalizedMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result<Object>> missingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("@ExceptionHandler(MissingServletRequestParameterException.class): ", e);
        return ResponseEntity.ok(Result.error(e.getLocalizedMessage()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Result<Object>> nullPointerException(NullPointerException e) {
        log.error("@ExceptionHandler(NullPointerException.class): ", e);
        return ResponseEntity.ok(Result.error("目标对象或值为空: " + e.getLocalizedMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Result<Object>> throwable(Throwable throwable) {
        log.error("@ExceptionHandler(Throwable.class): ", throwable);
        return ResponseEntity.ok(Result.error(throwable.getLocalizedMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Object>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("@ExceptionHandler(MethodArgumentNotValidException.class): ", e);
        return ResponseEntity.ok(Result.error(e.getLocalizedMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Result<Object>> bindException(BindException e) {
        log.error("@ExceptionHandler(BindException.class): ", e);
        return ResponseEntity.ok(Result.error(e.getLocalizedMessage()));
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<Result<Object>> badSqlGrammarException(BadSqlGrammarException e) {
        log.error("@ExceptionHandler(BindException.class): ", e);
        return ResponseEntity.ok(Result.error(e.getLocalizedMessage()));
    }

}
