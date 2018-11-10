package io.github.danielwii.wyf.security;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * 给 filter 提供解析认证 header 的方法
 */
public interface Authenticatable {

    Authentication authenticate(HttpServletRequest request);

}
