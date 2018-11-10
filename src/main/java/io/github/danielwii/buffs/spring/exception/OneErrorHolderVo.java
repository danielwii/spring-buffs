package io.github.danielwii.buffs.spring.exception;

import lombok.Data;

import java.util.List;

/**
 * 构造标准的错误响应格式
 */
@Data
public class OneErrorHolderVo {

    private Error error = new Error();

    @Data
    static class Error {
        private String code;
        private String type;
        private String message;
        private String exception;
        private Object additional;
        private String traceId;
        private List<Object> details;
        private List<Object> errors;
    }

}
