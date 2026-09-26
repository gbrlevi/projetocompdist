package com.distrischool.school_core_service.event;

import java.time.Instant;

public record TeacherAssignedEvent(
        Long professorId,
        String professorNome,
        String professorEmail,
        Long turmaId,
        String turmaCodigo,
        String turmaNome,
        Instant occurredAt
) {
}
