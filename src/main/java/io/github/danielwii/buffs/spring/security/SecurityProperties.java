package io.github.danielwii.buffs.spring.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Data
@Configuration("security")
@ConfigurationProperties("app.security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();

    @Data
    static class Jwt {
        private String secret;
        private String header      = "Authorization";
        private String tokenPrefix = "Bearer";
        private Long   expiration  = TimeUnit.DAYS.toSeconds(7);
    }

}
