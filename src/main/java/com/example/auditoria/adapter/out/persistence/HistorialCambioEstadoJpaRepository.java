package com.example.auditoria.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la bitácora histórica append-only.
 */
@Repository
public interface HistorialCambioEstadoJpaRepository extends JpaRepository<HistorialCambioEstadoJpaEntity, Long> {

    List<HistorialCambioEstadoJpaEntity> findByHallazgoIdOrderByFechaHoraAsc(UUID hallazgoId);
}
