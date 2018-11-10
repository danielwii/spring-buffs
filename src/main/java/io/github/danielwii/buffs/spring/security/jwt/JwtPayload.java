package io.github.danielwii.wyf.security.jwt;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Data
public class JwtPayload {
    private String         issuer;
    /**
     * the seconds after expiration, use system configured if not setup
     */
    private Long           expiration;
    private Device         device;
    private OffsetDateTime notBefore;
    @NonNull
    private UserDetails    userDetails;

    @Builder.Default
    private String id = UUID.randomUUID().toString();
}
