package io.github.danielwii.buffs.spring.exception;

import org.springframework.http.HttpStatus;

public class OneExceptionError extends OneException {
    OneExceptionError(OneErrorHolder errorHolder) {
        super(errorHolder);
    }

    OneExceptionError(OneErrorHolder errorHolder, HttpStatus status) {
        super(errorHolder, status);
    }
}
