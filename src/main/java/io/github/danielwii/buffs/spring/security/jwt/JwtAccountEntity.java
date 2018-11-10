package io.github.danielwii.buffs.spring.security.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.danielwii.buffs.spring.infrastructure.AbstractBaseVersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.OffsetDateTime;

@ToString(exclude = "password")
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class JwtAccountEntity extends AbstractBaseVersionEntity {

    @Column(nullable = false)
    private String username;

    @JsonIgnore
    private String password;

    private boolean expired;

    private boolean enabled;

    private boolean locked;

    private OffsetDateTime lastPasswordResetDate;

}
