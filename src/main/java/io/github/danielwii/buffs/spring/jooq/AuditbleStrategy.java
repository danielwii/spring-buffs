package io.github.danielwii.buffs.spring.jooq;

import io.github.danielwii.buffs.spring.shared.Auditable;
import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

import java.util.Collections;
import java.util.List;

public class AuditbleStrategy extends DefaultGeneratorStrategy {

    @Override
    public List<String> getJavaClassImplements(Definition definition, Mode mode) {
        if (!definition.getName().toLowerCase().startsWith("tr") && mode.equals(Mode.RECORD)) {
            return Collections.singletonList(Auditable.class.getName());
        } else {
            return Collections.emptyList();
        }
    }

}
