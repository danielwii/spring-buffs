package io.github.danielwii.wyf.infrastructure;

import io.github.danielwii.wyf.helper.DateHelper;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Data
@MappedSuperclass
public abstract class AbstractBaseEntity implements Serializable {

    /**
     * - The TABLE identifier strategy does not scale when increasing the number of database connections.
     *   More, even for one database connection, the identifier generation response time is 10 times greater
     *   than when using IDENTITY or SEQUENCE.
     *
     * - IDENTITY generator disables JDBC batch inserts
     *   execute the JDBC batch inserts with a different framework, like jOOQ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = DateHelper.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = DateHelper.now();
    }

}
