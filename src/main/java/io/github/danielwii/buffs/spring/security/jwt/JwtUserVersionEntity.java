package io.github.danielwii.wyf.security.jwt;

import io.github.danielwii.wyf.infrastructure.AbstractBaseVersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class JwtUserVersionEntity extends AbstractBaseVersionEntity {

    private String nickname;

}
