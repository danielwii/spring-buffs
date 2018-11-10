package io.github.danielwii.buffs.spring.jooq;

import lombok.NonNull;

public interface IRecordRepository<E, R> {

    void verifyExistSelector(@NonNull Long id);

}
