package com.example.auditoria.usecase.port.out;

/**
 * Modelo de vista / proyección para el conteo de hallazgos agrupados por categoría.
 */
public record ConteoCategoria(
    String categoria,
    long total
) {}
