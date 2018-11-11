package io.github.danielwii.buffs.spring.security.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.danielwii.buffs.spring.exception.OneError;
import io.github.danielwii.buffs.spring.exception.OneErrorHolderMapper;
import io.github.danielwii.buffs.spring.exception.OneErrorHolderVO;
import io.github.danielwii.buffs.spring.exception.OneException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        OneException oneException;
        if (authException instanceof BadCredentialsException) {
            oneException = OneError.BAD_CREDENTIALS.build().throwable(authException);
        } else {
            oneException = OneError.UNAUTHORIZED.build().throwable(authException);
        }

        OneErrorHolderVO errorHolderVO = OneErrorHolderMapper.INSTANCE.toVO(oneException.getErrorHolder());

        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        // response.sendError(oneException.getStatus().value(), gson.toJson(errorHolderVO));
        response.setStatus(oneException.getStatus().value());
        response.getWriter().write(gson.toJson(errorHolderVO));
    }
}
