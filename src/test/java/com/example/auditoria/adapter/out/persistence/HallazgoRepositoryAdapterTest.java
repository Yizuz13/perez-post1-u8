package com.example.auditoria.adapter.out.persistence;

import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.domain.model.PlanRemediacion;
import com.example.auditoria.domain.model.Severidad;
import com.example.auditoria.usecase.port.out.CambioEstadoView;
import com.example.auditoria.usecase.port.out.ConteoCategoria;
import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.out.PromedioCategoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Pruebas de Integración de Persistencia (H2, Adaptadores, Proyecciones y Bitácora Append-Only)")
class HallazgoRepositoryAdapterTest {

    @Autowired
    private HallazgoRepositoryAdapter hallazgoAdapter;

    @Autowired
    private HistorialAuditoriaAdapter historialAdapter;

    @Autowired
    private HallazgoJpaRepository hallazgoJpaRepository;

    @Autowired
    private HistorialCambioEstadoJpaRepository historialRepository;

    @org.junit.jupiter.api.BeforeEach
    void cleanDatabase() {
        historialRepository.deleteAll();
        hallazgoJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Debe persistir un hallazgo y recuperarlo correctamente con sus atributos y Value Objects")
    void debePersistirYRecuperarHallazgo() {
        HallazgoAuditoria nuevo = HallazgoAuditoria.crearNuevo(
            "Inyección SQL en módulo de reportes",
            "Parámetro 'filtro' concatenado directamente en consulta JDBC",
            "Seguridad Aplicativa",
            Severidad.CRITICA,
            LocalDate.of(2026, 9, 10)
        );

        HallazgoAuditoria guardado = hallazgoAdapter.guardar(nuevo);
        assertNotNull(guardado);
        assertEquals(nuevo.getId().valor(), guardado.getId().valor());

        Optional<HallazgoAuditoria> buscado = hallazgoAdapter.buscarPorId(guardado.getId());
        assertTrue(buscado.isPresent());
        assertEquals("Inyección SQL en módulo de reportes", buscado.get().getTitulo());
        assertEquals(EstadoHallazgo.ABIERTO, buscado.get().getEstado());
    }

    @Test
    @DisplayName("Debe persistir transiciones con PlanRemediacion y fecha de cierre, y calcular métricas H2 correctamente")
    void debeEjecutarMetricasAgregadasEnH2() {
        // Crear Hallazgo 1: Categoría 'Seguridad', cerrado tras 5 días
        HallazgoAuditoria h1 = HallazgoAuditoria.crearNuevo(
            "Vulnerabilidad A",
            "Desc A",
            "Seguridad",
            Severidad.ALTA,
            LocalDate.of(2026, 9, 1)
        );
        h1.iniciarRemediacion(new PlanRemediacion("Plan A", "Dev 1", LocalDate.of(2026, 9, 5)));
        h1.cerrar("Resuelto A", LocalDate.of(2026, 9, 6)); // 5 días de diferencia
        hallazgoAdapter.guardar(h1);

        // Crear Hallazgo 2: Categoría 'Seguridad', cerrado tras 15 días
        HallazgoAuditoria h2 = HallazgoAuditoria.crearNuevo(
            "Vulnerabilidad B",
            "Desc B",
            "Seguridad",
            Severidad.MEDIA,
            LocalDate.of(2026, 9, 1)
        );
        h2.iniciarRemediacion(new PlanRemediacion("Plan B", "Dev 2", LocalDate.of(2026, 9, 10)));
        h2.cerrar("Resuelto B", LocalDate.of(2026, 9, 16)); // 15 días de diferencia
        hallazgoAdapter.guardar(h2);

        // Crear Hallazgo 3: Categoría 'Redes', en estado ABIERTO (no cerrado)
        HallazgoAuditoria h3 = HallazgoAuditoria.crearNuevo(
            "Vulnerabilidad C",
            "Desc C",
            "Redes",
            Severidad.BAJA,
            LocalDate.of(2026, 9, 20)
        );
        hallazgoAdapter.guardar(h3);

        // Validar conteo total y por estado
        DashboardAuditoriaView dashboard = hallazgoAdapter.obtenerDashboard();
        assertEquals(3, dashboard.totalHallazgos());
        assertEquals(1, dashboard.totalAbiertos());
        assertEquals(0, dashboard.totalEnRemediacion());
        assertEquals(2, dashboard.totalCerrados());

        // Validar conteos por categoría
        List<ConteoCategoria> conteos = dashboard.conteoPorCategoria();
        assertEquals(2, conteos.size());

        // Validar promedio de días de resolución en H2 (Seguridad: (5 + 15) / 2 = 10.0 días)
        List<PromedioCategoria> promedios = dashboard.promedioDiasPorCategoria();
        assertFalse(promedios.isEmpty());
        PromedioCategoria promSeguridad = promedios.stream()
            .filter(p -> "Seguridad".equalsIgnoreCase(p.categoria()))
            .findFirst()
            .orElseThrow();

        assertEquals(10.0, promSeguridad.promedioDiasResolucion(), 0.01);
    }

    @Test
    @DisplayName("HistorialAuditoriaAdapter debe comportarse como bitácora append-only ordenada cronológicamente")
    void debeRegistrarBitacoraAppendOnly() {
        HallazgoId hallazgoId = HallazgoId.generar();

        // Registrar evento inicial
        historialAdapter.registrarCambio(
            hallazgoId,
            null,
            EstadoHallazgo.ABIERTO,
            "auditor1",
            "Apertura inicial del hallazgo"
        );

        // Registrar transición a EN_REMEDIACION
        historialAdapter.registrarCambio(
            hallazgoId,
            EstadoHallazgo.ABIERTO,
            EstadoHallazgo.EN_REMEDIACION,
            "analista_seguridad",
            "Aprobación del plan de mitigación"
        );

        // Registrar transición a CERRADO
        historialAdapter.registrarCambio(
            hallazgoId,
            EstadoHallazgo.EN_REMEDIACION,
            EstadoHallazgo.CERRADO,
            "lider_auditoria",
            "Verificación de controles satisfactoria"
        );

        List<CambioEstadoView> historial = historialAdapter.consultarHistorialPorHallazgo(hallazgoId);
        assertEquals(3, historial.size());
        assertEquals(EstadoHallazgo.ABIERTO, historial.get(0).estadoNuevo());
        assertEquals(EstadoHallazgo.EN_REMEDIACION, historial.get(1).estadoNuevo());
        assertEquals(EstadoHallazgo.CERRADO, historial.get(2).estadoNuevo());
        assertEquals("auditor1", historial.get(0).usuarioResponsable());
        assertEquals("lider_auditoria", historial.get(2).usuarioResponsable());
    }
}
