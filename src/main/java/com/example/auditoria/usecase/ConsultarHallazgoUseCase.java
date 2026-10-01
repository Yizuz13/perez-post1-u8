package com.example.auditoria.usecase;

import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;

import java.util.List;

/**
 * Caso de uso: Consultar información detallada de hallazgos individuales o listado global.
 */
public interface ConsultarHallazgoUseCase {

    HallazgoAuditoria consultarPorId(HallazgoId id);

    List<HallazgoAuditoria> listarTodos();
}
