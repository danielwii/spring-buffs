package io.github.danielwii.buffs.spring.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class OneException extends RuntimeException {

    private final OneErrorHolder errorHolder;
    private final HttpStatus     status;

    public OneException(OneErrorHolder errorHolder) {
        super(errorHolder.getMessage());
        this.errorHolder = errorHolder;
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public OneException(OneErrorHolder errorHolder, HttpStatus status) {
        super(errorHolder.getMessage());
        this.errorHolder = errorHolder;
        this.status = status;
    }

    public OneException errors(List<?> errors) {
        errorHolder.setErrors(errors);
        return this;
    }

    public OneException additional(Object additional) {
        errorHolder.setAdditional(additional);
        return this;
    }

    public static OneException recognize(Exception exception) {
        if (exception instanceof OneException) {
            return (OneException) exception;
        }

        // 422
        if (exception instanceof MethodArgumentNotValidException) {
            List<ObjectError> errors = ((MethodArgumentNotValidException) exception).getBindingResult().getAllErrors();
            return new OneExceptionAssembler(OneError.UNPROCESSABLE_ENTITY, errors, exception).build();
        }

        // 422
        if (exception instanceof MissingServletRequestParameterException) {
            return new OneExceptionAssembler(OneError.UNPROCESSABLE_ENTITY, exception).build();
        }

        // 400
        if (exception instanceof HttpMessageNotReadableException) {
            return new OneExceptionAssembler(OneError.BAD_REQUEST, exception).build();
        }

        // 415
        if (exception instanceof HttpMediaTypeNotSupportedException) {
            return new OneExceptionAssembler(OneError.UNSUPPORTED_MEDIA_TYPE, exception).build();
        }

        // 暂未识别的异常
        OneException oneException = new OneExceptionAssembler(OneError.INTERNAL_EXCEPTION, exception.getMessage(), exception).build();
        log.warn("Unrecognized Error", oneException);
        return oneException;
    }

    @Override
    public String getMessage() {
        return errorHolder.getMessage();
    }

}
