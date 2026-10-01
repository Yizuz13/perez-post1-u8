package com.example.auditoria.usecase.port.out;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia y consulta de hallazgos de auditoría.
 * En Clean Architecture, desacopla el caso de uso de la tecnología de base de datos.
 */
public interface HallazgoRepositoryPort {

    HallazgoAuditoria guardar(HallazgoAuditoria hallazgo);

    Optional<HallazgoAuditoria> buscarPorId(HallazgoId id);

    List<HallazgoAuditoria> listarTodos();

    long contarTotal();

    long contarPorEstado(EstadoHallazgo estado);

    List<ConteoCategoria> contarPorCategoria();

    List<PromedioCategoria> calcularPromedioDiasPorCategoria();

    DashboardAuditoriaView obtenerDashboard();
}
