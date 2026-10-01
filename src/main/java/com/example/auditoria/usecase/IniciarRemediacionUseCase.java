package com.example.auditoria.usecase;

import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.domain.model.PlanRemediacion;

/**
 * Caso de uso: Asignar un plan de remediación e iniciar el proceso de mitigación.
 */
public interface IniciarRemediacionUseCase {

    HallazgoAuditoria iniciarRemediacion(
        HallazgoId id,
        PlanRemediacion plan,
        String usuarioResponsable,
        String observacion
    );
}
