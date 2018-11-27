package io.github.danielwii.buffs.spring.exception;

import java.util.List;

public class OneExceptionAssembler {

    private IOneError error;
    private String    message;
    private Object[]  params;
    private Throwable cause;
    private List      errors;
    private Object    additional;

    public OneExceptionAssembler(IOneError error) {
        this.error = error;
        this.message = error.getMessageTemplate();
    }

    public OneExceptionAssembler(IOneError error, String message) {
        this.error = error;
        this.message = message;
    }

    public OneExceptionAssembler(IOneError error, Throwable cause) {
        this.error = error;
        this.message = error.getMessageTemplate();
        this.cause = cause;
    }

    public OneExceptionAssembler(IOneError error, List errors) {
        this.error = error;
        this.errors = errors;
    }

    public OneExceptionAssembler(String message, Throwable cause) {
        this.error = OneError.INTERNAL_EXCEPTION;
        this.message = message;
        this.cause = cause;
    }

    public OneExceptionAssembler(IOneError error, String message, Throwable cause) {
        this.error = error;
        this.message = message;
        this.cause = cause;
    }

    public OneExceptionAssembler(IOneError error, List errors, Throwable cause) {
        this.error = error;
        this.message = error.getMessageTemplate();
        this.errors = errors;
        this.cause = cause;
    }

    private OneExceptionAssembler params(Object... params) {
        this.params = params;
        return this;
    }

    private OneExceptionAssembler additional(Object additional) {
        this.additional = additional;
        return this;
    }

    public OneException build() {
        String message = this.message;
        if (params == null) {
            message = String.format(error.getMessageTemplate(), params);
        }
        OneErrorHolder errorHolder = OneErrorHolder.builder()
                .error(error)
                .errors(errors)
                .message(message)
                .cause(cause)
                .additional(additional)
                .build();
        return error.getErrorFunction() != null
               ? error.getErrorFunction().apply(errorHolder)
               : new OneExceptionError(errorHolder, error.getStatus());
    }

}
