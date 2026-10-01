package com.example.auditoria.adapter.out.persistence.projection;

/**
 * Proyección Spring Data JPA para la consulta agregada de conteo por categoría.
 */
public interface ConteoCategoriaProjection {

    String getCategoria();

    Long getTotal();
}
