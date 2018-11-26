package io.github.danielwii.buffs.spring.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

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
        this.status = status;
    }

    public void setErrors(List<?> errors) {
        errorHolder.setErrors(errors);
    }

    @Override
    public String getMessage() {
        return errorHolder.getMessage();
    }

}
