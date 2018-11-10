package io.github.danielwii.wyf.security.jwt;

import io.github.danielwii.wyf.infrastructure.AbstractBaseVersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import java.time.OffsetDateTime;

/**
 * used to handle session lifecycle at server side so that we can reject token in any time
 */
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class JwtAuthSessionEntity extends AbstractBaseVersionEntity {

    private OffsetDateTime expirationTime;

    private Boolean enabled;

    private String signInIp;

    private String audience;

    private String uuid;

    @PrePersist
    public void prePersist() {
        if (enabled == null) enabled = true;
    }

}
