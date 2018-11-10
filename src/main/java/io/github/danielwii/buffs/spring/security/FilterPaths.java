package io.github.danielwii.buffs.spring.security;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

public enum FilterPaths {
    ;

    private static AntPathMatcher matcher = new AntPathMatcher();
    private static Set<String> jwtProtectPatterns = new LinkedHashSet<>();

    public static void addJwtProtectPattern(String... patterns) {
        if (patterns != null) {
            jwtProtectPatterns.addAll(Arrays.asList(patterns));
        }
    }

    public static String[] getAllProtectedPatterns() {
        String[] patterns = new String[]{};
        return jwtProtectPatterns.toArray(patterns);
    }

    public static boolean isProtectedByJwt(@NotNull String path) {
        return jwtProtectPatterns.stream().anyMatch(protectPattern -> matcher.match(protectPattern, path));
    }
}
