package io.github.danielwii.buffs.spring.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
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
        this.status = Optional.ofNullable(status).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public OneException message(String message, Object... params) {
        if (StringUtils.isNotBlank(message)) {
            errorHolder.setMessage(ObjectUtils.isEmpty(params) ? String.format(message, params) : message);
        }
        return this;
    }

    public OneException throwable(Throwable throwable) {
        errorHolder.setException(throwable);
        return this;
    }

    public OneException additional(Object additional) {
        errorHolder.setAdditional(additional);
        return this;
    }

    public OneException errors(List errors) {
        errorHolder.setErrors(errors);
        return this;
    }
}
