package com.distrischool.school_core_service.event;

import com.distrischool.school_core_service.tecnico.Cargo;

import java.time.Instant;

/** Não inclui o CPF: o payload trafega por outros serviços e acaba em logs. */
public record StaffCreatedEvent(
        Long tecnicoId,
        String nome,
        String email,
        Cargo cargo,
        String setor,
        Instant occurredAt
) {
}
