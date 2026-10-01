package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.Severidad;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Implementación pura del caso de uso RegistrarHallazgoUseCase.
 * Libre de anotaciones de Spring (@Service/@Component) para preservar la independencia
 * de Clean Architecture.
 */
public class RegistrarHallazgoUseCaseImpl implements RegistrarHallazgoUseCase {

    private final HallazgoRepositoryPort hallazgoRepositoryPort;
    private final HistorialAuditoriaPort historialAuditoriaPort;

    public RegistrarHallazgoUseCaseImpl(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        this.hallazgoRepositoryPort = Objects.requireNonNull(hallazgoRepositoryPort, "El repositorio no puede ser nulo");
        this.historialAuditoriaPort = Objects.requireNonNull(historialAuditoriaPort, "El puerto de historial no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria registrar(
        String titulo,
        String descripcion,
        String categoria,
        Severidad severidad,
        LocalDate fechaDeteccion,
        String usuarioResponsable,
        String observacion
    ) {
        HallazgoAuditoria nuevoHallazgo = HallazgoAuditoria.crearNuevo(
            titulo,
            descripcion,
            categoria,
            severidad,
            fechaDeteccion
        );

        HallazgoAuditoria guardado = hallazgoRepositoryPort.guardar(nuevoHallazgo);

        String responsableAudit = (usuarioResponsable != null && !usuarioResponsable.isBlank())
            ? usuarioResponsable
            : "SISTEMA";
        String observacionAudit = (observacion != null && !observacion.isBlank())
            ? observacion
            : "Registro inicial del hallazgo en estado ABIERTO";

        historialAuditoriaPort.registrarCambio(
            guardado.getId(),
            null,
            guardado.getEstado(),
            responsableAudit,
            observacionAudit
        );

        return guardado;
    }
}
