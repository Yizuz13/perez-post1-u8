package com.example.auditoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para la solicitud de cierre formal de un hallazgo.
 */
public record CerrarRequest(
    @NotBlank(message = "El motivo de cierre del hallazgo es obligatorio")
    @Size(max = 1000, message = "El motivo de cierre no puede superar los 1000 caracteres")
    String motivoCierre,

    LocalDate fechaCierre,

    String usuarioResponsable,

    String observacion
) {}
