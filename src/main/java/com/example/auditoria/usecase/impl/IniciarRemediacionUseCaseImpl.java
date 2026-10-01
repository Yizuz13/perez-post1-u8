package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.exception.HallazgoNotFoundException;
import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.domain.model.PlanRemediacion;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;

import java.util.Objects;

/**
 * Implementación pura del caso de uso IniciarRemediacionUseCase.
 */
public class IniciarRemediacionUseCaseImpl implements IniciarRemediacionUseCase {

    private final HallazgoRepositoryPort hallazgoRepositoryPort;
    private final HistorialAuditoriaPort historialAuditoriaPort;

    public IniciarRemediacionUseCaseImpl(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        this.hallazgoRepositoryPort = Objects.requireNonNull(hallazgoRepositoryPort, "El repositorio no puede ser nulo");
        this.historialAuditoriaPort = Objects.requireNonNull(historialAuditoriaPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria iniciarRemediacion(
        HallazgoId id,
        PlanRemediacion plan,
        String usuarioResponsable,
        String observacion
    ) {
        Objects.requireNonNull(id, "El ID de hallazgo es obligatorio");
        Objects.requireNonNull(plan, "El plan de remediación es obligatorio");

        HallazgoAuditoria hallazgo = hallazgoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new HallazgoNotFoundException(id));

        EstadoHallazgo estadoAnterior = hallazgo.getEstado();

        // Ejecutar transición en el Aggregate Root (valida la máquina de estados)
        hallazgo.iniciarRemediacion(plan);

        HallazgoAuditoria actualizado = hallazgoRepositoryPort.guardar(hallazgo);

        String responsableAudit = (usuarioResponsable != null && !usuarioResponsable.isBlank())
            ? usuarioResponsable
            : "SISTEMA";
        String observacionAudit = (observacion != null && !observacion.isBlank())
            ? observacion
            : "Transición a EN_REMEDIACION con plan asignado a " + plan.responsable();

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
