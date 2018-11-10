package io.github.danielwii.buffs.spring.infrastructure;

import java.sql.Timestamp;

public interface Auditable {

    Timestamp getCreatedAt();

    void setCreatedAt(Timestamp createdAt);

    Timestamp getUpdatedAt();

    void setUpdatedAt(Timestamp updatedAt);

}
