package com.example.auditoria.usecase.port.out;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoId;

import java.util.List;

/**
 * Puerto de salida para el registro y consulta de la bitácora histórica
 * inmutable (append-only audit trail) de los cambios de estado de los hallazgos.
 */
public interface HistorialAuditoriaPort {

    void registrarCambio(
        HallazgoId hallazgoId,
        EstadoHallazgo estadoAnterior,
        EstadoHallazgo estadoNuevo,
        String usuarioResponsable,
        String observacion
    );

    List<CambioEstadoView> consultarHistorialPorHallazgo(HallazgoId hallazgoId);
}
