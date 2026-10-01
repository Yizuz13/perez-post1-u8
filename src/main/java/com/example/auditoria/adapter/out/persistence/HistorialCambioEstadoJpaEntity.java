package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.model.EstadoHallazgo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA append-only que representa la bitácora inmutable de cambios de estado
 * de los hallazgos de auditoría (Audit Trail).
 *
 * Criterio Arquitectónico:
 * No admite actualizaciones (UPDATE) ni eliminaciones (DELETE); cada evento
 * de cambio de estado se registra como un nuevo renglón histórico.
 */
@Entity
@Table(name = "historial_cambios_estado")
public class HistorialCambioEstadoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hallazgo_id", nullable = false, updatable = false)
    private UUID hallazgoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 30, updatable = false)
    private EstadoHallazgo estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 30, updatable = false)
    private EstadoHallazgo estadoNuevo;

    @Column(name = "usuario_responsable", nullable = false, length = 150, updatable = false)
    private String usuarioResponsable;

    @Column(name = "observacion", length = 1000, updatable = false)
    private String observacion;

    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    public HistorialCambioEstadoJpaEntity() {
    }

    public HistorialCambioEstadoJpaEntity(
        UUID hallazgoId,
        EstadoHallazgo estadoAnterior,
        EstadoHallazgo estadoNuevo,
        String usuarioResponsable,
        String observacion,
        LocalDateTime fechaHora
    ) {
        this.hallazgoId = hallazgoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.usuarioResponsable = usuarioResponsable;
        this.observacion = observacion;
        this.fechaHora = fechaHora;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public UUID getHallazgoId() {
        return hallazgoId;
    }

    public EstadoHallazgo getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoHallazgo getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public String getObservacion() {
        return observacion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}
