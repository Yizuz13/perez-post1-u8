package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.adapter.out.persistence.projection.ConteoCategoriaProjection;
import com.example.auditoria.adapter.out.persistence.projection.PromedioCategoriaProjection;
import com.example.auditoria.domain.model.EstadoHallazgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad HallazgoJpaEntity.
 * Expone métodos CRUD y consultas agregadas optimizadas para proyecciones.
 */
@Repository
public interface HallazgoJpaRepository extends JpaRepository<HallazgoJpaEntity, UUID> {

    long countByEstado(EstadoHallazgo estado);

    @Query("SELECT h.categoria AS categoria, COUNT(h) AS total " +
           "FROM HallazgoJpaEntity h " +
           "GROUP BY h.categoria " +
           "ORDER BY h.categoria ASC")
    List<ConteoCategoriaProjection> contarPorCategoria();

    @Query(value = "SELECT h.categoria AS categoria, " +
                   "AVG(DATEDIFF('DAY', h.fecha_deteccion, h.fecha_cierre)) AS promedioDias " +
                   "FROM hallazgos h " +
                   "WHERE h.fecha_cierre IS NOT NULL " +
                   "GROUP BY h.categoria " +
                   "ORDER BY h.categoria ASC",
           nativeQuery = true)
    List<PromedioCategoriaProjection> calcularPromedioDiasResolucionPorCategoria();
}
