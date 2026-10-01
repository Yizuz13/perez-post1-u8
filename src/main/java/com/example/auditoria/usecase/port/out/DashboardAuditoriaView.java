package com.example.auditoria.usecase.port.out;

import java.util.List;

/**
 * Modelo de vista que consolida las métricas gerenciales y de control
 * del dashboard de auditoría (CQRS liviano).
 */
public record DashboardAuditoriaView(
    long totalHallazgos,
    long totalAbiertos,
    long totalEnRemediacion,
    long totalCerrados,
    long totalReabiertos,
    List<ConteoCategoria> conteoPorCategoria,
    List<PromedioCategoria> promedioDiasPorCategoria
) {}
