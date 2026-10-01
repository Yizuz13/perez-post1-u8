package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.usecase.port.out.CambioEstadoView;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Adaptador de persistencia que implementa HistorialAuditoriaPort.
 * Maneja la inserción exclusiva (append-only) de eventos de cambio de estado.
 */
@Component
public class HistorialAuditoriaAdapter implements HistorialAuditoriaPort {

    private final HistorialCambioEstadoJpaRepository historialRepository;

    public HistorialAuditoriaAdapter(HistorialCambioEstadoJpaRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    @Transactional
    public void registrarCambio(
        HallazgoId hallazgoId,
        EstadoHallazgo estadoAnterior,
        EstadoHallazgo estadoNuevo,
        String usuarioResponsable,
        String observacion
    ) {
        HistorialCambioEstadoJpaEntity entidad = new HistorialCambioEstadoJpaEntity(
            hallazgoId.valor(),
            estadoAnterior,
            estadoNuevo,
            usuarioResponsable,
            observacion,
            LocalDateTime.now()
        );
        historialRepository.save(entidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CambioEstadoView> consultarHistorialPorHallazgo(HallazgoId hallazgoId) {
        List<HistorialCambioEstadoJpaEntity> entidades =
            historialRepository.findByHallazgoIdOrderByFechaHoraAsc(hallazgoId.valor());

        return entidades.stream()
            .map(e -> new CambioEstadoView(
                e.getId(),
                HallazgoId.desde(e.getHallazgoId()),
                e.getEstadoAnterior(),
                e.getEstadoNuevo(),
                e.getUsuarioResponsable(),
                e.getObservacion(),
                e.getFechaHora()
            ))
            .toList();
    }
}
