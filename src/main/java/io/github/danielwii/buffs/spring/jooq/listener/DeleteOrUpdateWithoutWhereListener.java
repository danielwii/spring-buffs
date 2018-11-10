package io.github.danielwii.buffs.spring.jooq.listener;

import org.jooq.ExecuteContext;
import org.jooq.impl.DefaultExecuteListener;

public class DeleteOrUpdateWithoutWhereListener extends DefaultExecuteListener {

    @Override
    public void renderEnd(ExecuteContext ctx) {
        if (ctx.sql().matches("^(?i:(UPDATE|DELETE)(?!.* WHERE ).*)$")) {
            throw new DeleteOrUpdateWithoutWhereException();
        }
    }
}

class DeleteOrUpdateWithoutWhereException extends RuntimeException {}
