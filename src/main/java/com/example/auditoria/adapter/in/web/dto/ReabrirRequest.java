package com.example.auditoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para la solicitud de reapertura de un hallazgo cerrado.
 */
public record ReabrirRequest(
    @NotBlank(message = "La justificación de la reapertura es obligatoria")
    @Size(max = 1000, message = "La justificación no puede superar los 1000 caracteres")
    String justificacionReapertura,

    String usuarioResponsable,

    String observacion
) {}
