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

    private static OneException getOneException(IOneError error, OneErrorHolder errorHolder) {
        return error.getErrorFunction() != null
               ? error.getErrorFunction().apply(errorHolder)
               : new OneExceptionError(errorHolder, error.getStatus());
    }

    public static OneException recognize(Exception exception) {
        if (exception instanceof OneException) {
            return (OneException) exception;
        }

        // 422
        if (exception instanceof MethodArgumentNotValidException) {
            List<ObjectError> errors = ((MethodArgumentNotValidException) exception).getBindingResult().getAllErrors();
            return build(OneError.UNPROCESSABLE_ENTITY, errors, exception);
        }

        // 422
        if (exception instanceof MissingServletRequestParameterException) {
            return build(OneError.UNPROCESSABLE_ENTITY, exception);
        }

        // 400
        if (exception instanceof HttpMessageNotReadableException) {
            return build(OneError.BAD_REQUEST, exception);
        }

        // 415
        if (exception instanceof HttpMediaTypeNotSupportedException) {
            return build(OneError.UNSUPPORTED_MEDIA_TYPE, exception);
        }

        // 暂未识别的异常
        OneException oneException = build(OneError.INTERNAL_EXCEPTION, exception.getMessage(), exception);
        log.warn("Unrecognized Error", oneException);
        return oneException;
    }

    public static OneException build(IOneError error) {
        return build(error, error.getMessageTemplate());
    }

    public static OneException build(IOneError error, String message) {
        return build(error, message, null);
    }

    public static OneException build(IOneError error, Throwable cause) {
        return build(error, error.getMessageTemplate(), cause);
    }

    public static OneException build(IOneError error, List errors) {
        return build(error, errors, null);
    }

    public static OneException build(String message, Throwable cause) {
        return build(OneError.INTERNAL_EXCEPTION, message, cause);
    }

    public static OneException build(IOneError error, String message, Throwable cause) {
        OneErrorHolder errorHolder = OneErrorHolder.builder()
                .error(error)
                .message(message)
                .cause(cause)
                .build();
        return getOneException(error, errorHolder);
    }

    private static OneException build(IOneError error, List errors, Throwable cause) {
        OneErrorHolder errorHolder = OneErrorHolder.builder()
                .error(error)
                .errors(errors)
                .cause(cause)
                .build();
        return getOneException(error, errorHolder);
    }

}
