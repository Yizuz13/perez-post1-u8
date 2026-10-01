package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.Severidad;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de respuesta para la representación REST completa de un hallazgo de auditoría.
 */
public record HallazgoResponse(
    UUID id,
    String titulo,
    String descripcion,
    String categoria,
    Severidad severidad,
    EstadoHallazgo estado,
    PlanRemediacionResponse planRemediacion,
    LocalDate fechaDeteccion,
    LocalDate fechaCierre,
    String motivoCierre,
    String justificacionReapertura
) {

    public static HallazgoResponse fromDomain(HallazgoAuditoria domain) {
        if (domain == null) {
            return null;
        }
        return new HallazgoResponse(
            domain.getId().valor(),
            domain.getTitulo(),
            domain.getDescripcion(),
            domain.getCategoria(),
            domain.getSeveridad(),
            domain.getEstado(),
            PlanRemediacionResponse.fromDomain(domain.getPlanRemediacion()),
            domain.getFechaDeteccion(),
            domain.getFechaCierre(),
            domain.getMotivoCierre(),
            domain.getJustificacionReapertura()
        );
    }
}
