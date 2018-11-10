package io.github.danielwii.wyf.jooq;

import lombok.NonNull;

public interface IRecordRepository<E, R> {

    void verifyExistSelector(@NonNull Long id);

}
