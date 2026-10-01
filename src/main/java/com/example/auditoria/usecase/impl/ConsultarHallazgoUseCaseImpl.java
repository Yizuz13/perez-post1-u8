package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.exception.HallazgoNotFoundException;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;

import java.util.List;
import java.util.Objects;

/**
 * Implementación pura del caso de uso ConsultarHallazgoUseCase.
 */
public class ConsultarHallazgoUseCaseImpl implements ConsultarHallazgoUseCase {

    private final HallazgoRepositoryPort hallazgoRepositoryPort;

    public ConsultarHallazgoUseCaseImpl(HallazgoRepositoryPort hallazgoRepositoryPort) {
        this.hallazgoRepositoryPort = Objects.requireNonNull(hallazgoRepositoryPort, "El repositorio no puede ser nulo");
    }

    @Override
    public HallazgoAuditoria consultarPorId(HallazgoId id) {
        Objects.requireNonNull(id, "El ID de hallazgo es obligatorio");
        return hallazgoRepositoryPort.buscarPorId(id)
            .orElseThrow(() -> new HallazgoNotFoundException(id));
    }

    @Override
    public List<HallazgoAuditoria> listarTodos() {
        return hallazgoRepositoryPort.listarTodos();
    }
}
