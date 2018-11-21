package io.github.danielwii.buffs.spring.exception;

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
public enum OneError implements IOneError {

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

    @Contract(value = "null -> false", pure = true)
    public boolean eq(OneException e) {
        return e != null && this.equals(e.getErrorHolder().getError());
    }
}
