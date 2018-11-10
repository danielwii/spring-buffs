package io.github.danielwii.buffs.spring.jooq;

import org.jooq.Record;
import org.jooq.impl.TableImpl;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SoftDeleteCheckpoint {

    Class<? extends TableImpl<? extends Record>> record();

}
