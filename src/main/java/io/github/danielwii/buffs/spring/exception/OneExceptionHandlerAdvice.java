package io.github.danielwii.buffs.spring.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class OneExceptionHandlerAdvice {

    @ExceptionHandler(OneException.class)
    public ResponseEntity<OneErrorHolderVO> handleException(OneException exception) {
        log.warn(exception.getMessage());
        OneErrorHolderVO errorHolderVO = OneErrorHolderMapper.INSTANCE.toVO(exception.getErrorHolder());
        return ResponseEntity.status(exception.getStatus()).body(errorHolderVO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OneErrorHolderVO> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return handleException(OneError.recognize(exception));
    }

}
