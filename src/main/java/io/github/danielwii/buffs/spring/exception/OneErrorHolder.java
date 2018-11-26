package io.github.danielwii.buffs.spring.exception;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OneErrorHolder {

    /**
     * 业务异常
     */
    private IOneError error;
    /**
     * 基本错误信息
     */
    private String    message;
    /**
     * 原始错误信息，非业务异常定义
     */
    private Throwable cause;
    /**
     * 一个预留的错误信息占位，用于自定义输出
     */
    private Object    additional;
    /**
     * 包含一系列更基础的错误信息
     * e.g. ValidationError 中包含所有的格式错误信息
     */
    private List      errors;

}
