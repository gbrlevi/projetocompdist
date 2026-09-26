package com.distrischool.school_core_service.event;

import java.time.Instant;

public record StudentCreatedEvent(
        Long alunoId,
        String matricula,
        String nome,
        String email,
        Instant occurredAt
) {
}
