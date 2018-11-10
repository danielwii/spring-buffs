package io.github.danielwii.wyf.exception;

import org.jetbrains.annotations.Contract;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;
import java.util.function.Function;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
public enum OneError {

    // @formatter:off

    // --------------------------------------------------------------
    // client side
    // https://stackoverflow.com/a/52098667/3480445(400 vs 422 for Client Error Request)
    // https://www.codetinkerer.com/2015/12/04/choosing-an-http-status-code.html
    // --------------------------------------------------------------

    BAD_REQUEST               ( "0x4000", "Bad Request", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE    ( "0x4150", "unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    UNPROCESSABLE_ENTITY      ( "0x4220", "Unprocessable Entity", HttpStatus.UNPROCESSABLE_ENTITY),
    MISSING_REQUEST_PARAMETER ( "0x4220", "Unprocessable Entity", HttpStatus.UNPROCESSABLE_ENTITY),

    /**
     * 由于缺少、无效或过期的令牌，请求未通过身份验证
     */
    UNAUTHORIZED              ("0x4010", "Unauthorized or Authentication Failed", HttpStatus.UNAUTHORIZED),

    /**
     * 错误的认证信息，无法获得令牌
     */
    BAD_CREDENTIALS           ("0x4011", "Bad Credentials", HttpStatus.UNAUTHORIZED),

    /**
     * 客户端没有相应的权限
     */
    NO_AUTHORIZATION          ("0x4030", "No Authorization for This Action", HttpStatus.FORBIDDEN),
    TOO_MANY_REQUESTS         ("0x4290", "Too Many Requests", HttpStatus.TOO_MANY_REQUESTS),

    // --------------------------------------------------------------
    // server side
    // --------------------------------------------------------------

    INTERNAL_EXCEPTION        ("0x5000", "Internal Exception"),

    // --------------------------------------------------------------
    // orm
    // --------------------------------------------------------------

    ENTITY_NOT_AVAILABLE      ("0xE000", "'%s' is not available for entity '%s'"),
    ENTITY_ALREADY_EXISTS     ("0xE000", "'%s' is already exists for entity '%s'"),

    // --------------------------------------------------------------
    // jooq
    // --------------------------------------------------------------

    JOOQ_DELETE_OR_UPDATE_WITHOUT_WHERE("0xQ000", "DeleteOrUpdateWithoutWhereException"),

    // @formatter:on
    ;

    private HttpStatus status;
    private String     code;
    private String     messageTemplate;

    private Function<OneErrorHolder, ? extends OneException> errorFunction;

    OneError(@NotNull String code, @NotNull String messageTemplate) {
        this.code = code;
        this.messageTemplate = messageTemplate;
    }

    OneError(@NotNull String code, @NotNull String messageTemplate, @NotNull HttpStatus status) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.status = status;
    }

    OneError(@NotNull String code,
             @NotNull String messageTemplate,
             @NotNull Function<OneErrorHolder, ? extends OneException> errorFunction) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.errorFunction = errorFunction;
    }

    public static OneException recognize(Exception exception) {
        if (exception instanceof OneException) {
            return (OneException) exception;
        }

        // 422
        if (exception instanceof MethodArgumentNotValidException) {
            List<ObjectError> allErrors = ((MethodArgumentNotValidException) exception).getBindingResult().getAllErrors();
            return UNPROCESSABLE_ENTITY.build().errors(allErrors).throwable(exception);
        }

        // 422
        if (exception instanceof MissingServletRequestParameterException) {
            return UNPROCESSABLE_ENTITY.build().throwable(exception);
        }

        // 400
        if (exception instanceof HttpMessageNotReadableException) {
            return BAD_REQUEST.build().throwable(exception);
        }

        // 415
        if (exception instanceof HttpMediaTypeNotSupportedException) {
            return UNSUPPORTED_MEDIA_TYPE.build().throwable(exception);
        }

        // 暂未识别的异常
        OneException oneException = INTERNAL_EXCEPTION.build().message(exception.getMessage()).throwable(exception);
        log.warn(String.format("Unrecognized Error: [%s]", oneException));
        return oneException;
    }

    public OneException build(Object... params) {
        OneErrorHolder errorHolder = OneErrorHolder.builder()
            .error(this)
            .message(params != null ? String.format(messageTemplate, params) : messageTemplate)
            .build();
        return errorFunction != null ? errorFunction.apply(errorHolder) : new OneExceptionError(errorHolder, status);
    }

    @Contract(value = "null -> false", pure = true)
    public boolean eq(OneException e) {
        return e != null && this.equals(e.getErrorHolder().getError());
    }
}
