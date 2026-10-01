package com.example.auditoria.usecase;

import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;

/**
 * Caso de uso: Consultar métricas agregadas del dashboard de auditoría (CQRS liviano).
 */
public interface ObtenerDashboardAuditoriaUseCase {

    DashboardAuditoriaView obtenerDashboard();
}
