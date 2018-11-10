package io.github.danielwii.wyf.security.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;

@ToString(exclude = "password")
@Builder
public class JwtUser implements UserDetails {

    @Getter
    private final Long   accountId;
    @Getter
    private final Long   userId;
    @Getter
    private final String nickname;
    private final String username;
    private final String password;

    private final boolean enabled;
    private final boolean accountExpired;
    private final boolean credentialsExpired;
    private final boolean locked;

    @Getter
    private final OffsetDateTime lastPasswordResetDate;

    private final Collection<? extends GrantedAuthority> authorities;

    public static JwtUser of(JwtAccountEntity account,
                             JwtUserVersionEntity user,
                             Collection<? extends GrantedAuthority> authorities) {
        return JwtUser.builder()
            .accountId(account.getId())
            .userId(user.getId())
            .nickname(user.getNickname())
            .username(account.getUsername())
            .password(account.getPassword())
            .authorities(authorities)
            .enabled(account.isEnabled())
            //.accountExpired(account.isExpired())
            //.credentialsExpired(credentials.isExpired())
            .locked(account.isLocked())
            .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
