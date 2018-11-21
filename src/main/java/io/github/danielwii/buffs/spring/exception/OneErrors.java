package io.github.danielwii.buffs.spring.exception;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum OneErrors {
    ;

    public static OneException recognize(Exception exception) {
        if (exception instanceof OneException) {
            return (OneException) exception;
        }

        // 422
        if (exception instanceof MethodArgumentNotValidException) {
            List<ObjectError> allErrors = ((MethodArgumentNotValidException) exception).getBindingResult().getAllErrors();
            return build(OneError.UNPROCESSABLE_ENTITY).errors(allErrors).throwable(exception);
        }

        // 422
        if (exception instanceof MissingServletRequestParameterException) {
            return build(OneError.UNPROCESSABLE_ENTITY).throwable(exception);
        }

        // 400
        if (exception instanceof HttpMessageNotReadableException) {
            return build(OneError.BAD_REQUEST).throwable(exception);
        }

        // 415
        if (exception instanceof HttpMediaTypeNotSupportedException) {
            return build(OneError.UNSUPPORTED_MEDIA_TYPE).throwable(exception);
        }

        // 暂未识别的异常
        OneException oneException = build(OneError.INTERNAL_EXCEPTION).message(exception.getMessage()).throwable(exception);
        log.warn("Unrecognized Error", oneException);
        return oneException;
    }

    public static OneException build(OneError error, Object... params) {
        OneErrorHolder errorHolder = OneErrorHolder.builder()
                .error(error)
                .message(params != null ? String.format(error.getMessageTemplate(), params) : error.getMessageTemplate())
                .build();
        return error.getErrorFunction() != null ? error.getErrorFunction().apply(errorHolder) : new OneExceptionError(errorHolder, error.getStatus());
    }

}
