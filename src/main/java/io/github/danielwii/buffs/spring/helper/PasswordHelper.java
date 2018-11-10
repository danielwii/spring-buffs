package io.github.danielwii.buffs.spring.helper;

import com.google.common.hash.Hashing;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;

public enum PasswordHelper {
    ;

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String mixedWithSalt(@NotNull String salt, @NotNull String rawPassword) {
        return String.format("%s#%s", salt, Hashing.sha512().hashUnencodedChars(rawPassword));
    }

    public static String encrypt(@NotNull String password) {
        return passwordEncoder.encode(password);
    }

    public static String encrypt(@NotNull String salt, @NotNull String rawPassword) {
        return passwordEncoder.encode(mixedWithSalt(salt, rawPassword));
    }

    public static boolean matches(@NotNull String salt, @NotNull String rawPassword, @NotNull String encrypted) {
        return passwordEncoder.matches(mixedWithSalt(salt, rawPassword), encrypted);
    }

}
