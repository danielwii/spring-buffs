package io.github.danielwii.wyf.jooq.listener;

import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.ExecuteContext;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultExecuteListener;

@Slf4j
public class PrettyPrinter extends DefaultExecuteListener {

    /**
     * Hook into the query execution lifecycle before executing queries
     */
    @Override
    public void executeStart(ExecuteContext ctx) {

        // Create a new DSLContext for logging rendering purposes
        // This DSLContext doesn't need a connection, only the SQLDialect...
        DSLContext create = DSL.using(ctx.dialect(),

            // ... and the flag for pretty-printing
            new Settings().withRenderFormatted(true));

        // If we're executing a query
        if (ctx.query() != null) {
            log.info("[PrettyPrinter] {}", create.renderInlined(ctx.query()));
        }

        // If we're executing a routine
        else if (ctx.routine() != null) {
            log.info("[PrettyPrinter] {}", create.renderInlined(ctx.routine()));
        }
    }
}
