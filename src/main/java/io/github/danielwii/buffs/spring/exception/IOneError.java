package io.github.danielwii.buffs.spring.exception;

import org.springframework.http.HttpStatus;

import java.util.function.Function;

public interface IOneError {

    String getMessageTemplate();

    Function<OneErrorHolder, ? extends OneException> getErrorFunction();

    HttpStatus getStatus();

}
