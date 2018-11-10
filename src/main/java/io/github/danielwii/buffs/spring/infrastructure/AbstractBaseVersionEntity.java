package io.github.danielwii.wyf.infrastructure;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public abstract class AbstractBaseVersionEntity extends AbstractBaseEntity {

    @Version
    private int version;

}
