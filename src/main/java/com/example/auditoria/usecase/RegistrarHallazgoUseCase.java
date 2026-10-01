package com.example.auditoria.usecase;

import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.Severidad;

import java.time.LocalDate;

/**
 * Caso de uso: Registrar un nuevo hallazgo de auditoría en estado ABIERTO.
 */
public interface RegistrarHallazgoUseCase {

    HallazgoAuditoria registrar(
        String titulo,
        String descripcion,
        String categoria,
        Severidad severidad,
        LocalDate fechaDeteccion,
        String usuarioResponsable,
        String observacion
    );
}
