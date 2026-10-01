package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.port.out.CambioEstadoView;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;

import java.util.List;
import java.util.Objects;

/**
 * Implementación pura del caso de uso ConsultarHistorialUseCase.
 */
public class ConsultarHistorialUseCaseImpl implements ConsultarHistorialUseCase {

    private final HistorialAuditoriaPort historialAuditoriaPort;

    public ConsultarHistorialUseCaseImpl(HistorialAuditoriaPort historialAuditoriaPort) {
        this.historialAuditoriaPort = Objects.requireNonNull(historialAuditoriaPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public List<CambioEstadoView> consultarPorHallazgo(HallazgoId id) {
        Objects.requireNonNull(id, "El ID de hallazgo es obligatorio");
        return historialAuditoriaPort.consultarHistorialPorHallazgo(id);
    }
}
