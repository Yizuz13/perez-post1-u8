package com.example.auditoria.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object inmutable que representa la propuesta formal de mitigación
 * o plan de acción correctivo para subsanar un hallazgo de auditoría.
 */
public record PlanRemediacion(
    String descripcionAccion,
    String responsable,
    LocalDate fechaCompromiso
) {

    public PlanRemediacion {
        if (descripcionAccion == null || descripcionAccion.isBlank()) {
            throw new IllegalArgumentException("La descripción de la acción del plan de remediación es obligatoria");
        }
        if (responsable == null || responsable.isBlank()) {
            throw new IllegalArgumentException("El responsable asignado al plan de remediación es obligatorio");
        }
        Objects.requireNonNull(fechaCompromiso, "La fecha de compromiso del plan de remediación es obligatoria");
    }
}
