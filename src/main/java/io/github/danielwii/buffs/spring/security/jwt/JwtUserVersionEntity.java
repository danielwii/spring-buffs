package io.github.danielwii.buffs.spring.security.jwt;

import io.github.danielwii.buffs.spring.infrastructure.AbstractBaseVersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.MappedSuperclass;

@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class JwtUserVersionEntity extends AbstractBaseVersionEntity {

    private String nickname;

}
