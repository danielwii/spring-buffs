package io.github.danielwii.buffs.spring.security.jwt;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import io.github.danielwii.buffs.spring.security.SecurityProperties;

public class JWTImportSelector implements ImportSelector {
    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata importingClassMetadata) {
        return new String[]{
                JwtAuthenticationEntryPoint.class.getName(),
                JwtAuthenticationService.class.getName(),
                SecurityProperties.class.getName(),
        };
    }
}
