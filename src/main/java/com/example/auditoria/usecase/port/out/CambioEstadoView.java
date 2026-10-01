package com.example.auditoria.usecase.port.out;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoId;

import java.time.LocalDateTime;

/**
 * Modelo de vista inmutable para representar un registro de auditoría
 * de cambio de estado (bitácora append-only).
 */
public record CambioEstadoView(
    Long id,
    HallazgoId hallazgoId,
    EstadoHallazgo estadoAnterior,
    EstadoHallazgo estadoNuevo,
    String usuarioResponsable,
    String observacion,
    LocalDateTime fechaHora
) {}
