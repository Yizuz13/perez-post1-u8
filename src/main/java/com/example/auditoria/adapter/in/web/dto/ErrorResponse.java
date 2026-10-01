package com.example.auditoria.adapter.in.web.dto;

import java.time.LocalDateTime;

/**
 * Estructura estándar para respuestas de error de la API REST.
 */
public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String mensaje,
    String path
) {}
