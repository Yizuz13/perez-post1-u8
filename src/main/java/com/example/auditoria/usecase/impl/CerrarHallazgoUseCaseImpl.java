package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.exception.HallazgoNotFoundException;
import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Implementación pura del caso de uso CerrarHallazgoUseCase.
 */
public class CerrarHallazgoUseCaseImpl implements CerrarHallazgoUseCase {

    private final HallazgoRepositoryPort hallazgoRepositoryPort;
    private final HistorialAuditoriaPort historialAuditoriaPort;

    public CerrarHallazgoUseCaseImpl(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        this.hallazgoRepositoryPort = Objects.requireNonNull(hallazgoRepositoryPort, "El repositorio no puede ser nulo");
        this.historialAuditoriaPort = Objects.requireNonNull(historialAuditoriaPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria cerrar(
        HallazgoId id,
        String motivoCierre,
        LocalDate fechaCierre,
        String usuarioResponsable,
        String observacion
    ) {
        Objects.requireNonNull(id, "El ID de hallazgo es obligatorio");

        HallazgoAuditoria hallazgo = hallazgoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new HallazgoNotFoundException(id));

        EstadoHallazgo estadoAnterior = hallazgo.getEstado();

        // Ejecutar transición en el Aggregate Root
        hallazgo.cerrar(motivoCierre, fechaCierre);

        HallazgoAuditoria actualizado = hallazgoRepositoryPort.guardar(hallazgo);

        String responsableAudit = (usuarioResponsable != null && !usuarioResponsable.isBlank())
            ? usuarioResponsable
            : "SISTEMA";
        String observacionAudit = (observacion != null && !observacion.isBlank())
            ? observacion
            : ("Cierre de hallazgo. Motivo: " + motivoCierre);

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
