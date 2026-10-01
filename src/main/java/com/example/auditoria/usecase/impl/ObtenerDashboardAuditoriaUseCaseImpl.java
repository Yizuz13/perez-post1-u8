package com.example.auditoria.usecase.impl;

import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;

import java.util.Objects;

/**
 * Implementación pura del caso de uso ObtenerDashboardAuditoriaUseCase.
 * Orquesta la recuperación de datos consolidados para el dashboard (CQRS liviano).
 */
public class ObtenerDashboardAuditoriaUseCaseImpl implements ObtenerDashboardAuditoriaUseCase {

    private final HallazgoRepositoryPort hallazgoRepositoryPort;

    public ObtenerDashboardAuditoriaUseCaseImpl(HallazgoRepositoryPort hallazgoRepositoryPort) {
        this.hallazgoRepositoryPort = Objects.requireNonNull(hallazgoRepositoryPort, "El repositorio no puede ser nulo");
    }

    @Override
    public DashboardAuditoriaView obtenerDashboard() {
        return hallazgoRepositoryPort.obtenerDashboard();
    }
}
