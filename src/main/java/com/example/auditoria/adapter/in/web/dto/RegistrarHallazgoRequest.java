package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.model.Severidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para la solicitud de registro de un nuevo hallazgo de auditoría.
 */
public record RegistrarHallazgoRequest(
    @NotBlank(message = "El título del hallazgo es obligatorio")
    @Size(max = 250, message = "El título no puede exceder los 250 caracteres")
    String titulo,

    @NotBlank(message = "La descripción del hallazgo es obligatoria")
    String descripcion,

    @NotBlank(message = "La categoría del hallazgo es obligatoria")
    String categoria,

    @NotNull(message = "El nivel de severidad es obligatorio (CRITICA, ALTA, MEDIA, BAJA)")
    Severidad severidad,

    LocalDate fechaDeteccion,

    String usuarioResponsable,

    String observacion
) {}
