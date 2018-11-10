package io.github.danielwii.wyf.security.jwt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;

import io.github.danielwii.wyf.exception.OneError;
import io.github.danielwii.wyf.exception.OneException;
import io.github.danielwii.wyf.helper.DateHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SigningKeyResolverAdapter;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationService {

    private static final String                   AUDIENCE_UNKNOWN = "unknown";
    private static final String                   AUDIENCE_WEB     = "web";
    private static final String                   AUDIENCE_MOBILE  = "mobile";
    private static final String                   AUDIENCE_TABLET  = "tablet";
    private static       Supplier<String>         saltSupplier;
    private static       Function<String, String> secretKeySupplier;

    @Value("#{security.jwt.secret}")
    private String secret;
    @Value("#{security.jwt.tokenPrefix}")
    private String tokenPrefix;
    @Value("#{security.jwt.header}")
    private String tokenHeader;
    @Value("#{security.jwt.expiration}")
    private Long   expiration;

    public static void init(Supplier<String> saltSupplier, Function<String, String> secretKeySupplier) {
        JwtAuthenticationService.saltSupplier = saltSupplier;
        JwtAuthenticationService.secretKeySupplier = secretKeySupplier;
    }

    public Authentication getAuthentication(HttpServletRequest request,
                                            Function<Claims, JwtAccountEntity> loadAccount,
                                            Function<Claims, JwtUserVersionEntity> loadUser,
                                            Function<Claims, UserDetails> loadUserDetails,
                                            Function<Claims, JwtAuthSessionEntity> loadAuthSession) throws OneException {
        String token = request.getHeader(tokenHeader);
        if (token == null) {
            // cause non-secured api exists, pass through directly
            return null;
        }

        log.trace("parse token: [{}]", token);

        if (!token.startsWith(String.format("%s ", tokenPrefix))) {
            log.trace("token prefix not matched: [{}]", tokenPrefix);
            throw OneError.BAD_CREDENTIALS.build().additional(ImmutableMap.of("tokenPrefix", tokenPrefix));
        }

        Claims               claims      = extractClaims(token);
        JwtAccountEntity     account     = loadAccount.apply(claims);
        JwtUserVersionEntity user        = loadUser.apply(claims);
        UserDetails          userDetails = loadUserDetails.apply(claims);

        if (account != null) {

            if (!account.isEnabled()) {
                throw OneError.BAD_CREDENTIALS.build().message("account is not enabled: %s", account);
            }

            if (account.isExpired()) {
                throw OneError.BAD_CREDENTIALS.build().message("account is expired: %s", account);
            }

            if (account.isLocked()) {
                throw OneError.BAD_CREDENTIALS.build().message("account is locked: %s", account);
            }

            JwtAuthSessionEntity authSession = loadAuthSession.apply(claims);

            if (authSession != null && !authSession.getEnabled()) {
                if (authSession.getExpirationTime().isAfter(DateHelper.now())) {
                    throw OneError.NO_AUTHORIZATION.build("token is expired: %s", authSession.getExpirationTime());
                }
                throw OneError.BAD_CREDENTIALS.build().message("token is not valid");
            }

            return AuthenticatedUser.builder()
                .username(account.getUsername())
                .authenticated(true)
                .account(account)
                .user(user)
                .userDetails(userDetails)
                .build();
        }

        throw OneError.BAD_CREDENTIALS.build().message("account is not found");
    }

    public String generateToken(JwtPayload jwtPayload) {
        String         issuer    = Optional.ofNullable(jwtPayload.getIssuer()).orElse("/default");
        String         subject   = jwtPayload.getUserDetails().getUsername();
        String         audience  = generateAudience(jwtPayload.getDevice());
        OffsetDateTime notBefore = Optional.ofNullable(jwtPayload.getNotBefore()).orElse(DateHelper.now());

        Long           defaultExpiration = Optional.ofNullable(jwtPayload.getExpiration()).orElse(this.expiration);
        OffsetDateTime expirationTime    = generateExpirationDate(defaultExpiration);


        Map<String, Object> claims = Maps.newHashMap();
        claims.put(Claims.ISSUER, issuer);
        claims.put(Claims.SUBJECT, subject);
        claims.put(Claims.AUDIENCE, audience);
        claims.put(Claims.EXPIRATION, expirationTime.toEpochSecond());
        claims.put(Claims.NOT_BEFORE, notBefore.toEpochSecond());
        claims.put(Claims.ISSUED_AT, DateHelper.now().toEpochSecond());
        claims.put(Claims.ID, jwtPayload.getId());

        log.debug("Claims is [{}]", claims);
        String secretKey = calcSecret(calcConcatenatedClaims(claims));

        return Jwts.builder()
            .setClaims(claims)
            .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS512)
            .compact();
    }

    public Boolean validateToken(String token, JwtUser user) {
        Claims claims         = extractClaims(token);
        String username       = claims.getSubject();
        Date   created        = claims.getIssuedAt();
        Date   expirationTime = claims.getExpiration();

        boolean isTokenNotExpired = DateHelper.toOffsetDateTime(expirationTime).isAfter(DateHelper.now());
        boolean isCreatedAfterLastPasswordReset = user.getLastPasswordResetDate() != null && DateHelper.toOffsetDateTime(created)
            .isAfter(user.getLastPasswordResetDate());

        return (username.equals(user.getUsername()) && isTokenNotExpired && isCreatedAfterLastPasswordReset);
    }

    private Claims extractClaims(String token) {
        String extractedToken = StringUtils.substringAfter(token, tokenPrefix).trim();

        return Jwts.parser()
            .setSigningKeyResolver(new SigningKeyResolverAdapter() {
                @Override
                public Key resolveSigningKey(JwsHeader header, Claims claims) {
                    log.debug("Claims is [{}]", claims);
                    return Keys.hmacShaKeyFor(calcSecret(calcConcatenatedClaims(claims)).getBytes());
                }
            })
            .parseClaimsJws(extractedToken)
            .getBody();
    }

    private String calcConcatenatedClaims(Map<String, Object> claims) {
        return String.format("%s:%s:%s:%s", claims.get(Claims.AUDIENCE), claims.get(Claims.ISSUER), claims
            .get(Claims.EXPIRATION), claims.get(Claims.SUBJECT));
    }

    private String calcConcatenatedClaims(Claims claims) {
        return String.format("%s:%s:%s:%s", claims.get(Claims.AUDIENCE), claims.get(Claims.ISSUER), claims
            .get(Claims.EXPIRATION), claims.get(Claims.SUBJECT));
    }

    private String calcSecret(String concatenated) {
        log.info("Secret is [{}]", concatenated);
        String salt = Optional.ofNullable(saltSupplier)
            .orElse(() -> Hashing.sha512().hashString(concatenated, StandardCharsets.UTF_8).toString())
            .get();

        return Optional.ofNullable(secretKeySupplier)
            .orElse(s -> Hashing.sha512().hashString(String.format("%s:%s", secret, s), StandardCharsets.UTF_8).toString())
            .apply(salt);
    }

    private OffsetDateTime generateExpirationDate(Long expiration) {
        return DateHelper.now().plusSeconds(expiration);
    }

    private String generateAudience(Device device) {
        if (device == null) {
            return AUDIENCE_UNKNOWN;
        }

        StringBuilder audience = new StringBuilder(device.getDevicePlatform().name());
        audience.append(":");

        if (device.isNormal()) audience.append(AUDIENCE_WEB);
        else if (device.isMobile()) audience.append(AUDIENCE_MOBILE);
        else if (device.isTablet()) audience.append(AUDIENCE_TABLET);
        else audience.append(AUDIENCE_UNKNOWN);

        return audience.toString();
    }

}
