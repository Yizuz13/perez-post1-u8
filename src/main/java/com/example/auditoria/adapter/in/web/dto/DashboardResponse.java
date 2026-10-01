package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.usecase.port.out.ConteoCategoria;
import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.out.PromedioCategoria;

import java.util.List;

/**
 * DTO de respuesta para el Dashboard de métricas gerenciales y de control.
 */
public record DashboardResponse(
    long totalHallazgos,
    long totalAbiertos,
    long totalEnRemediacion,
    long totalCerrados,
    long totalReabiertos,
    List<ConteoCategoria> conteoPorCategoria,
    List<PromedioCategoria> promedioDiasResolucionPorCategoria
) {

    public static DashboardResponse fromView(DashboardAuditoriaView view) {
        if (view == null) {
            return null;
        }
        return new DashboardResponse(
            view.totalHallazgos(),
            view.totalAbiertos(),
            view.totalEnRemediacion(),
            view.totalCerrados(),
            view.totalReabiertos(),
            view.conteoPorCategoria(),
            view.promedioDiasPorCategoria()
        );
    }
}
