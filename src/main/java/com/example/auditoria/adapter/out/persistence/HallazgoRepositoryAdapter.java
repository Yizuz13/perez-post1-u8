package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.adapter.out.persistence.projection.ConteoCategoriaProjection;
import com.example.auditoria.adapter.out.persistence.projection.PromedioCategoriaProjection;
import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.domain.model.PlanRemediacion;
import com.example.auditoria.usecase.port.out.ConteoCategoria;
import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.PromedioCategoria;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia que implementa HallazgoRepositoryPort.
 * Conecta los casos de uso con Spring Data JPA y traduce entre
 * entidades JPA y modelos del dominio.
 */
@Component
public class HallazgoRepositoryAdapter implements HallazgoRepositoryPort {

    private final HallazgoJpaRepository hallazgoJpaRepository;

    public HallazgoRepositoryAdapter(HallazgoJpaRepository hallazgoJpaRepository) {
        this.hallazgoJpaRepository = hallazgoJpaRepository;
    }

    @Override
    @Transactional
    public HallazgoAuditoria guardar(HallazgoAuditoria hallazgo) {
        HallazgoJpaEntity jpaEntity = toJpaEntity(hallazgo);
        HallazgoJpaEntity savedEntity = hallazgoJpaRepository.save(jpaEntity);
        return toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HallazgoAuditoria> buscarPorId(HallazgoId id) {
        return hallazgoJpaRepository.findById(id.valor())
            .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallazgoAuditoria> listarTodos() {
        return hallazgoJpaRepository.findAll()
            .stream()
            .map(this::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTotal() {
        return hallazgoJpaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoHallazgo estado) {
        return hallazgoJpaRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConteoCategoria> contarPorCategoria() {
        List<ConteoCategoriaProjection> projections = hallazgoJpaRepository.contarPorCategoria();
        return projections.stream()
            .map(p -> new ConteoCategoria(p.getCategoria(), p.getTotal() != null ? p.getTotal() : 0L))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromedioCategoria> calcularPromedioDiasPorCategoria() {
        List<PromedioCategoriaProjection> projections = hallazgoJpaRepository.calcularPromedioDiasResolucionPorCategoria();
        return projections.stream()
            .map(p -> new PromedioCategoria(
                p.getCategoria(),
                p.getPromedioDias() != null ? Math.round(p.getPromedioDias().doubleValue() * 100.0) / 100.0 : 0.0
            ))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardAuditoriaView obtenerDashboard() {
        long total = contarTotal();
        long abiertos = contarPorEstado(EstadoHallazgo.ABIERTO);
        long enRemediacion = contarPorEstado(EstadoHallazgo.EN_REMEDIACION);
        long cerrados = contarPorEstado(EstadoHallazgo.CERRADO);
        long reabiertos = contarPorEstado(EstadoHallazgo.REABIERTO);

        List<ConteoCategoria> conteos = contarPorCategoria();
        List<PromedioCategoria> promedios = calcularPromedioDiasPorCategoria();

        return new DashboardAuditoriaView(
            total,
            abiertos,
            enRemediacion,
            cerrados,
            reabiertos,
            conteos,
            promedios
        );
    }

    // =========================================================================
    // MAPEOS ENTRE CAPAS (DOMAIN <-> PERSISTENCE)
    // =========================================================================

    private HallazgoJpaEntity toJpaEntity(HallazgoAuditoria domain) {
        PlanRemediacion plan = domain.getPlanRemediacion();
        String descPlan = (plan != null) ? plan.descripcionAccion() : null;
        String respPlan = (plan != null) ? plan.responsable() : null;
        java.time.LocalDate fechaPlan = (plan != null) ? plan.fechaCompromiso() : null;

        return new HallazgoJpaEntity(
            domain.getId().valor(),
            domain.getTitulo(),
            domain.getDescripcion(),
            domain.getCategoria(),
            domain.getSeveridad(),
            domain.getEstado(),
            domain.getFechaDeteccion(),
            domain.getFechaCierre(),
            domain.getMotivoCierre(),
            domain.getJustificacionReapertura(),
            descPlan,
            respPlan,
            fechaPlan
        );
    }

    private HallazgoAuditoria toDomain(HallazgoJpaEntity entity) {
        PlanRemediacion plan = null;
        if (entity.getPlanDescripcionAccion() != null
            && entity.getPlanResponsable() != null
            && entity.getPlanFechaCompromiso() != null) {
            plan = new PlanRemediacion(
                entity.getPlanDescripcionAccion(),
                entity.getPlanResponsable(),
                entity.getPlanFechaCompromiso()
            );
        }

        return HallazgoAuditoria.reconstituir(
            HallazgoId.desde(entity.getId()),
            entity.getTitulo(),
            entity.getDescripcion(),
            entity.getCategoria(),
            entity.getSeveridad(),
            entity.getEstado(),
            plan,
            entity.getFechaDeteccion(),
            entity.getFechaCierre(),
            entity.getMotivoCierre(),
            entity.getJustificacionReapertura()
        );
    }
}
