package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.model.PlanRemediacion;

import java.time.LocalDate;

/**
 * DTO de respuesta para el Plan de Remediación.
 */
public record PlanRemediacionResponse(
    String descripcionAccion,
    String responsable,
    LocalDate fechaCompromiso
) {

    public static PlanRemediacionResponse fromDomain(PlanRemediacion domain) {
        if (domain == null) {
            return null;
        }
        return new PlanRemediacionResponse(
            domain.descripcionAccion(),
            domain.responsable(),
            domain.fechaCompromiso()
        );
    }
}
