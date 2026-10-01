package com.example.auditoria.usecase.port.out;

/**
 * Modelo de vista / proyección para el promedio de días transcurridos
 * en la resolución de hallazgos cerrados agrupados por categoría.
 */
public record PromedioCategoria(
    String categoria,
    double promedioDiasResolucion
) {}
