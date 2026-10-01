package com.example.auditoria.usecase;

import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;

/**
 * Caso de uso: Reabrir un hallazgo de auditoría previamente cerrado.
 */
public interface ReabrirHallazgoUseCase {

    HallazgoAuditoria reabrir(
        HallazgoId id,
        String justificacionReapertura,
        String usuarioResponsable,
        String observacion
    );
}
