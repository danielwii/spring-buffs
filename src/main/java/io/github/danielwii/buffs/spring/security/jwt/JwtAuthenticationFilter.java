package io.github.danielwii.buffs.spring.security.jwt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.github.danielwii.buffs.spring.security.FilterPaths;
import io.github.danielwii.buffs.spring.exception.OneError;
import io.github.danielwii.buffs.spring.exception.OneErrorHolderMapper;
import io.github.danielwii.buffs.spring.exception.OneErrorHolderVO;
import io.github.danielwii.buffs.spring.exception.OneException;
import io.github.danielwii.buffs.spring.security.Authenticatable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Gson gson = new GsonBuilder().serializeNulls().create();
    private Authenticatable securityService;

    public JwtAuthenticationFilter(Authenticatable securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();

        // fix spec path issue
        if (StringUtils.isEmpty(path)) {
            path = request.getPathInfo();
        }

        log.debug("request path is `{}`", path);

        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        if (FilterPaths.isProtectedByJwt(path)) {
            try {
                Authentication authentication = securityService.authenticate(request);

                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

                filterChain.doFilter(request, response);
            } catch (Exception e) {
                OneException oneException = OneError.recognize(e);
                OneErrorHolderVO errorHolderVO = OneErrorHolderMapper.INSTANCE.toVO(oneException.getErrorHolder());
                response.setStatus(oneException.getStatus().value());
                response.getWriter().write(gson.toJson(errorHolderVO));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
