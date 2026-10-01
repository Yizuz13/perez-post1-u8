package com.example.auditoria.usecase;

import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.usecase.port.out.CambioEstadoView;

import java.util.List;

/**
 * Caso de uso: Consultar la trazabilidad histórica inmutable de estados de un hallazgo.
 */
public interface ConsultarHistorialUseCase {

    List<CambioEstadoView> consultarPorHallazgo(HallazgoId id);
}
