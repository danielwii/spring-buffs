package io.github.danielwii.buffs.spring.exception;

import java.util.List;

import lombok.Data;

/**
 * 构造标准的错误响应格式
 */
@Data
public class OneErrorHolderVO {

    private Error error = new Error();

    @Data
    static class Error {
        private String       code;
        private String       type;
        private String       message;
        private String       cause;
        private Object       additional;
        private String       traceId;
        private List<Object> details;
        private List<Object> errors;
    }

}
