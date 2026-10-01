package com.example.auditoria.domain.model;

import com.example.auditoria.domain.exception.TransicionInvalidaException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Agregado Raíz (Aggregate Root) que encapsula la entidad principal del dominio
 * de auditoría y protege las invariantes y reglas de negocio de su ciclo de vida.
 *
 * Cumple estrictamente con Clean Architecture:
 * - Sin dependencias a frameworks ni persistencia.
 * - Sin setters públicos.
 * - Modificación de estado exclusivamente a través de métodos de intención de negocio.
 */
public class HallazgoAuditoria {

    private final HallazgoId id;
    private final String titulo;
    private final String descripcion;
    private final String categoria;
    private final Severidad severidad;
    private final LocalDate fechaDeteccion;

    private EstadoHallazgo estado;
    private PlanRemediacion planRemediacion;
    private LocalDate fechaCierre;
    private String motivoCierre;
    private String justificacionReapertura;

    /**
     * Constructor privado para forzar el uso de factorías de creación y reconstitución.
     */
    private HallazgoAuditoria(
        HallazgoId id,
        String titulo,
        String descripcion,
        String categoria,
        Severidad severidad,
        EstadoHallazgo estado,
        PlanRemediacion planRemediacion,
        LocalDate fechaDeteccion,
        LocalDate fechaCierre,
        String motivoCierre,
        String justificacionReapertura
    ) {
        this.id = Objects.requireNonNull(id, "El ID del hallazgo no puede ser nulo");
        this.titulo = validarNoVacio(titulo, "El título del hallazgo es obligatorio");
        this.descripcion = validarNoVacio(descripcion, "La descripción del hallazgo es obligatoria");
        this.categoria = validarNoVacio(categoria, "La categoría del hallazgo es obligatoria");
        this.severidad = Objects.requireNonNull(severidad, "La severidad del hallazgo es obligatoria");
        this.estado = Objects.requireNonNull(estado, "El estado del hallazgo no puede ser nulo");
        this.fechaDeteccion = Objects.requireNonNull(fechaDeteccion, "La fecha de detección es obligatoria");
        this.planRemediacion = planRemediacion;
        this.fechaCierre = fechaCierre;
        this.motivoCierre = motivoCierre;
        this.justificacionReapertura = justificacionReapertura;
    }

    /**
     * Fábrica para registrar un nuevo hallazgo en estado inicial ABIERTO.
     */
    public static HallazgoAuditoria crearNuevo(
        String titulo,
        String descripcion,
        String categoria,
        Severidad severidad,
        LocalDate fechaDeteccion
    ) {
        LocalDate fechaEfectiva = (fechaDeteccion != null) ? fechaDeteccion : LocalDate.now();
        return new HallazgoAuditoria(
            HallazgoId.generar(),
            titulo,
            descripcion,
            categoria,
            severidad,
            EstadoHallazgo.ABIERTO,
            null,
            fechaEfectiva,
            null,
            null,
            null
        );
    }

    /**
     * Fábrica para reconstituir un hallazgo existente desde la capa de persistencia.
     */
    public static HallazgoAuditoria reconstituir(
        HallazgoId id,
        String titulo,
        String descripcion,
        String categoria,
        Severidad severidad,
        EstadoHallazgo estado,
        PlanRemediacion planRemediacion,
        LocalDate fechaDeteccion,
        LocalDate fechaCierre,
        String motivoCierre,
        String justificacionReapertura
    ) {
        return new HallazgoAuditoria(
            id,
            titulo,
            descripcion,
            categoria,
            severidad,
            estado,
            planRemediacion,
            fechaDeteccion,
            fechaCierre,
            motivoCierre,
            justificacionReapertura
        );
    }

    // =========================================================================
    // MÉTODOS DE TRANSICIÓN DE ESTADO (MÁQUINA DE ESTADOS FINITA)
    // =========================================================================

    /**
     * Inicia el proceso de remediación asociando un plan de acción formal.
     * Transición válida: ABIERTO -> EN_REMEDIACION o REABIERTO -> EN_REMEDIACION
     *
     * @param plan Plan de remediación formalmente establecido.
     * @throws TransicionInvalidaException si el estado actual no permite la remediación.
     */
    public void iniciarRemediacion(PlanRemediacion plan) {
        this.estado.validarTransicion(EstadoHallazgo.EN_REMEDIACION);
        this.planRemediacion = Objects.requireNonNull(plan, "El plan de remediación es obligatorio para iniciar la remediación");
        this.estado = EstadoHallazgo.EN_REMEDIACION;
    }

    /**
     * Cierra formalmente el hallazgo cuando la remediación ha concluido.
     * Transición válida: EN_REMEDIACION -> CERRADO
     *
     * @param motivo Justificación o detalle de las acciones finales de cierre.
     * @param fechaCierre Fecha efectiva del cierre (no puede ser anterior a la detección).
     * @throws TransicionInvalidaException si el estado actual no es EN_REMEDIACION.
     */
    public void cerrar(String motivo, LocalDate fechaCierre) {
        this.estado.validarTransicion(EstadoHallazgo.CERRADO);
        this.motivoCierre = validarNoVacio(motivo, "El motivo de cierre es obligatorio");
        LocalDate fechaEfectiva = (fechaCierre != null) ? fechaCierre : LocalDate.now();

        if (fechaEfectiva.isBefore(this.fechaDeteccion)) {
            throw new IllegalArgumentException("La fecha de cierre (" + fechaEfectiva + ") no puede ser anterior a la fecha de detección (" + this.fechaDeteccion + ")");
        }

        this.fechaCierre = fechaEfectiva;
        this.estado = EstadoHallazgo.CERRADO;
    }

    /**
     * Reabre un hallazgo cerrado si se detecta recurrencia o ineficacia del control.
     * Transición válida: CERRADO -> REABIERTO
     *
     * @param justificacion Motivo auditado por el cual se reabre el hallazgo.
     * @throws TransicionInvalidaException si el estado actual no es CERRADO.
     */
    public void reabrir(String justificacion) {
        this.estado.validarTransicion(EstadoHallazgo.REABIERTO);
        this.justificacionReapertura = validarNoVacio(justificacion, "La justificación de reapertura es obligatoria");
        this.fechaCierre = null;
        this.estado = EstadoHallazgo.REABIERTO;
    }

    // =========================================================================
    // CONSULTAS Y GETTERS (INMUTABILIDAD EXTERNA - SIN SETTERS PÚBLICOS)
    // =========================================================================

    public HallazgoId getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public Severidad getSeveridad() {
        return severidad;
    }

    public EstadoHallazgo getEstado() {
        return estado;
    }

    public PlanRemediacion getPlanRemediacion() {
        return planRemediacion;
    }

    public Optional<PlanRemediacion> getPlanRemediacionOptional() {
        return Optional.ofNullable(planRemediacion);
    }

    public LocalDate getFechaDeteccion() {
        return fechaDeteccion;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public String getJustificacionReapertura() {
        return justificacionReapertura;
    }

    private static String validarNoVacio(String valor, String mensajeError) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensajeError);
        }
        return valor.trim();
    }
}
