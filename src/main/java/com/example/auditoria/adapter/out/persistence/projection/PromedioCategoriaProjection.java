package com.example.auditoria.adapter.out.persistence.projection;

/**
 * Proyección Spring Data JPA para la consulta agregada de promedio de días de resolución.
 * Se utiliza Number para garantizar compatibilidad con los tipos devueltos por AVG en H2 (Double/BigDecimal).
 */
public interface PromedioCategoriaProjection {

    String getCategoria();

    Number getPromedioDias();
}
