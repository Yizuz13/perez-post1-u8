package com.example.auditoria.usecase;

import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;

import java.time.LocalDate;

/**
 * Caso de uso: Cerrar formalmente un hallazgo de auditoría en estado de remediación.
 */
public interface CerrarHallazgoUseCase {

    HallazgoAuditoria cerrar(
        HallazgoId id,
        String motivoCierre,
        LocalDate fechaCierre,
        String usuarioResponsable,
        String observacion
    );
}
