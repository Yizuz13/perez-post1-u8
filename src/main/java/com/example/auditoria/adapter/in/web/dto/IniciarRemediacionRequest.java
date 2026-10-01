package com.example.auditoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para la solicitud de inicio de remediación asociando un plan de acción.
 */
public record IniciarRemediacionRequest(
    @NotBlank(message = "La descripción de la acción del plan de remediación es obligatoria")
    @Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
    String descripcionAccion,

    @NotBlank(message = "El responsable asignado al plan es obligatorio")
    @Size(max = 150, message = "El nombre del responsable no puede superar los 150 caracteres")
    String responsable,

    @NotNull(message = "La fecha de compromiso de entrega del plan es obligatoria")
    LocalDate fechaCompromiso,

    String usuarioResponsable,

    String observacion
) {}
