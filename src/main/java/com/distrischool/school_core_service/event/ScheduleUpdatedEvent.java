package com.distrischool.school_core_service.event;

import java.time.Instant;

public record ScheduleUpdatedEvent(
        Long turmaId,
        String turmaCodigo,
        Acao acao,
        Instant occurredAt
) {

    public enum Acao {
        CRIADA, ATUALIZADA, REMOVIDA
    }
}
