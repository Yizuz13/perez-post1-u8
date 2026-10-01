package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.Severidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad JPA para la persistencia relacional del hallazgo de auditoría en la tabla 'hallazgos'.
 */
@Entity
@Table(name = "hallazgos")
public class HallazgoJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "titulo", nullable = false, length = 250)
    private String titulo;

    @Column(name = "descripcion", nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "categoria", nullable = false, length = 100)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "severidad", nullable = false, length = 20)
    private Severidad severidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoHallazgo estado;

    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDate fechaDeteccion;

    @Column(name = "fecha_cierre")
    private LocalDate fechaCierre;

    @Column(name = "motivo_cierre", length = 1000)
    private String motivoCierre;

    @Column(name = "justificacion_reapertura", length = 1000)
    private String justificacionReapertura;

    // Atributos del Plan de Remediación embebidos en el registro relacional
    @Column(name = "plan_descripcion_accion", length = 1000)
    private String planDescripcionAccion;

    @Column(name = "plan_responsable", length = 150)
    private String planResponsable;

    @Column(name = "plan_fecha_compromiso")
    private LocalDate planFechaCompromiso;

    public HallazgoJpaEntity() {
    }

    public HallazgoJpaEntity(
        UUID id,
        String titulo,
        String descripcion,
        String categoria,
        Severidad severidad,
        EstadoHallazgo estado,
        LocalDate fechaDeteccion,
        LocalDate fechaCierre,
        String motivoCierre,
        String justificacionReapertura,
        String planDescripcionAccion,
        String planResponsable,
        LocalDate planFechaCompromiso
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.severidad = severidad;
        this.estado = estado;
        this.fechaDeteccion = fechaDeteccion;
        this.fechaCierre = fechaCierre;
        this.motivoCierre = motivoCierre;
        this.justificacionReapertura = justificacionReapertura;
        this.planDescripcionAccion = planDescripcionAccion;
        this.planResponsable = planResponsable;
        this.planFechaCompromiso = planFechaCompromiso;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public void setSeveridad(Severidad severidad) {
        this.severidad = severidad;
    }

    public EstadoHallazgo getEstado() {
        return estado;
    }

    public void setEstado(EstadoHallazgo estado) {
        this.estado = estado;
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public void setFechaDeteccion(LocalDate fechaDeteccion) {
        this.fechaDeteccion = fechaDeteccion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public void setMotivoCierre(String motivoCierre) {
        this.motivoCierre = motivoCierre;
    }

    public String getJustificacionReapertura() {
        return justificacionReapertura;
    }

    public void setJustificacionReapertura(String justificacionReapertura) {
        this.justificacionReapertura = justificacionReapertura;
    }

    public String getPlanDescripcionAccion() {
        return planDescripcionAccion;
    }

    public void setPlanDescripcionAccion(String planDescripcionAccion) {
        this.planDescripcionAccion = planDescripcionAccion;
    }

    public String getPlanResponsable() {
        return planResponsable;
    }

    public void setPlanResponsable(String planResponsable) {
        this.planResponsable = planResponsable;
    }

    public LocalDate getPlanFechaCompromiso() {
        return planFechaCompromiso;
    }

    public void setPlanFechaCompromiso(LocalDate planFechaCompromiso) {
        this.planFechaCompromiso = planFechaCompromiso;
    }
}
