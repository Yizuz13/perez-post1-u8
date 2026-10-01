package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.exception.HallazgoNotFoundException;
import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;

import java.util.Objects;

/**
 * Implementación pura del caso de uso ReabrirHallazgoUseCase.
 */
public class ReabrirHallazgoUseCaseImpl implements ReabrirHallazgoUseCase {

    private final HallazgoRepositoryPort hallazgoRepositoryPort;
    private final HistorialAuditoriaPort historialAuditoriaPort;

    public ReabrirHallazgoUseCaseImpl(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        this.hallazgoRepositoryPort = Objects.requireNonNull(hallazgoRepositoryPort, "El repositorio no puede ser nulo");
        this.historialAuditoriaPort = Objects.requireNonNull(historialAuditoriaPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria reabrir(
        HallazgoId id,
        String justificacionReapertura,
        String usuarioResponsable,
        String observacion
    ) {
        Objects.requireNonNull(id, "El ID de hallazgo es obligatorio");

        HallazgoAuditoria hallazgo = hallazgoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new HallazgoNotFoundException(id));

        EstadoHallazgo estadoAnterior = hallazgo.getEstado();

        // Ejecutar transición en el Aggregate Root
        hallazgo.reabrir(justificacionReapertura);

        HallazgoAuditoria actualizado = hallazgoRepositoryPort.guardar(hallazgo);

        String responsableAudit = (usuarioResponsable != null && !usuarioResponsable.isBlank())
            ? usuarioResponsable
            : "SISTEMA";
        String observacionAudit = (observacion != null && !observacion.isBlank())
            ? observacion
            : ("Reapertura de hallazgo. Justificación: " + justificacionReapertura);

        historialAuditoriaPort.registrarCambio(
            actualizado.getId(),
            estadoAnterior,
            actualizado.getEstado(),
            responsableAudit,
            observacionAudit
        );

        return actualizado;
    }
}
