package io.github.danielwii.wyf.jooq.listener;

import io.github.danielwii.wyf.infrastructure.Auditable;
import io.github.danielwii.wyf.helper.DateHelper;
import org.jooq.RecordContext;
import org.jooq.impl.DefaultRecordListener;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class AuditRecordListener extends DefaultRecordListener {

    @Override
    public void insertStart(RecordContext ctx) {
        if (ctx.record() instanceof Auditable) {
            OffsetDateTime now    = DateHelper.now();
            Auditable      record = (Auditable) ctx.record();
            record.setCreatedAt(Timestamp.from(now.toInstant()));
            record.setUpdatedAt(Timestamp.from(now.toInstant()));
        }
    }

    @Override
    public void updateStart(RecordContext ctx) {
        if (ctx.record() instanceof Auditable) {
            OffsetDateTime now    = DateHelper.now();
            Auditable      record = (Auditable) ctx.record();
            record.setUpdatedAt(Timestamp.from(now.toInstant()));
        }
    }

}
