package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.usecase.port.out.CambioEstadoView;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para cada registro de trazabilidad en la bitácora histórica.
 */
public record CambioEstadoResponse(
    Long id,
    UUID hallazgoId,
    EstadoHallazgo estadoAnterior,
    EstadoHallazgo estadoNuevo,
    String usuarioResponsable,
    String observacion,
    LocalDateTime fechaHora
) {

    public static CambioEstadoResponse fromView(CambioEstadoView view) {
        if (view == null) {
            return null;
        }
        return new CambioEstadoResponse(
            view.id(),
            view.hallazgoId().valor(),
            view.estadoAnterior(),
            view.estadoNuevo(),
            view.usuarioResponsable(),
            view.observacion(),
            view.fechaHora()
        );
    }
}
