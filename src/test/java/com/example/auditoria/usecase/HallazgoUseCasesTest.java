package com.example.auditoria.usecase;

import com.example.auditoria.domain.exception.HallazgoNotFoundException;
import com.example.auditoria.domain.exception.TransicionInvalidaException;
import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.domain.model.PlanRemediacion;
import com.example.auditoria.domain.model.Severidad;
import com.example.auditoria.usecase.impl.CerrarHallazgoUseCaseImpl;
import com.example.auditoria.usecase.impl.ConsultarHallazgoUseCaseImpl;
import com.example.auditoria.usecase.impl.ConsultarHistorialUseCaseImpl;
import com.example.auditoria.usecase.impl.IniciarRemediacionUseCaseImpl;
import com.example.auditoria.usecase.impl.ObtenerDashboardAuditoriaUseCaseImpl;
import com.example.auditoria.usecase.impl.ReabrirHallazgoUseCaseImpl;
import com.example.auditoria.usecase.impl.RegistrarHallazgoUseCaseImpl;
import com.example.auditoria.usecase.port.out.CambioEstadoView;
import com.example.auditoria.usecase.port.out.ConteoCategoria;
import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;
import com.example.auditoria.usecase.port.out.PromedioCategoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias de Casos de Uso (Capa de Aplicación Pura)")
class HallazgoUseCasesTest {

    @Mock
    private HallazgoRepositoryPort hallazgoRepositoryPort;

    @Mock
    private HistorialAuditoriaPort historialAuditoriaPort;

    private RegistrarHallazgoUseCase registrarUseCase;
    private IniciarRemediacionUseCase iniciarRemediacionUseCase;
    private CerrarHallazgoUseCase cerrarUseCase;
    private ReabrirHallazgoUseCase reabrirUseCase;
    private ConsultarHallazgoUseCase consultarUseCase;
    private ObtenerDashboardAuditoriaUseCase dashboardUseCase;
    private ConsultarHistorialUseCase historialUseCase;

    @BeforeEach
    void setUp() {
        registrarUseCase = new RegistrarHallazgoUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
        iniciarRemediacionUseCase = new IniciarRemediacionUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
        cerrarUseCase = new CerrarHallazgoUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
        reabrirUseCase = new ReabrirHallazgoUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
        consultarUseCase = new ConsultarHallazgoUseCaseImpl(hallazgoRepositoryPort);
        dashboardUseCase = new ObtenerDashboardAuditoriaUseCaseImpl(hallazgoRepositoryPort);
        historialUseCase = new ConsultarHistorialUseCaseImpl(historialAuditoriaPort);
    }

    @Test
    @DisplayName("RegistrarHallazgoUseCase debe persistir el hallazgo y registrar en la bitácora")
    void debeRegistrarHallazgoYBitacora() {
        when(hallazgoRepositoryPort.guardar(any(HallazgoAuditoria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        HallazgoAuditoria resultado = registrarUseCase.registrar(
            "Falta MFA en consola Cloud",
            "Cuentas administrativas sin segundo factor de autenticación",
            "Seguridad Cloud",
            Severidad.CRITICA,
            LocalDate.now(),
            "auditor_perez",
            "Detección durante revisión trimestral"
        );

        assertNotNull(resultado);
        assertEquals(EstadoHallazgo.ABIERTO, resultado.getEstado());
        verify(hallazgoRepositoryPort).guardar(any(HallazgoAuditoria.class));
        verify(historialAuditoriaPort).registrarCambio(
            eq(resultado.getId()),
            eq(null),
            eq(EstadoHallazgo.ABIERTO),
            eq("auditor_perez"),
            eq("Detección durante revisión trimestral")
        );
    }

    @Test
    @DisplayName("IniciarRemediacionUseCase debe cambiar estado a EN_REMEDIACION y registrar en bitácora")
    void debeIniciarRemediacion() {
        HallazgoAuditoria hallazgo = HallazgoAuditoria.crearNuevo(
            "Certificado SSL próximo a expirar",
            "Expira en 5 días",
            "Infraestructura",
            Severidad.ALTA,
            LocalDate.now()
        );

        when(hallazgoRepositoryPort.buscarPorId(hallazgo.getId())).thenReturn(Optional.of(hallazgo));
        when(hallazgoRepositoryPort.guardar(any(HallazgoAuditoria.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        PlanRemediacion plan = new PlanRemediacion("Renovar con Let's Encrypt y automatizar", "DevOps Lead", LocalDate.now().plusDays(2));

        HallazgoAuditoria actualizado = iniciarRemediacionUseCase.iniciarRemediacion(
            hallazgo.getId(),
            plan,
            "devops_user",
            "Plan cargado en Jira"
        );

        assertEquals(EstadoHallazgo.EN_REMEDIACION, actualizado.getEstado());
        verify(historialAuditoriaPort).registrarCambio(
            eq(hallazgo.getId()),
            eq(EstadoHallazgo.ABIERTO),
            eq(EstadoHallazgo.EN_REMEDIACION),
            eq("devops_user"),
            eq("Plan cargado en Jira")
        );
    }

    @Test
    @DisplayName("CerrarHallazgoUseCase debe validar existencia y lanzar NotFoundException si no existe")
    void debeLanzarNotFoundAlCerrarInexistente() {
        HallazgoId id = HallazgoId.generar();
        when(hallazgoRepositoryPort.buscarPorId(id)).thenReturn(Optional.empty());

        assertThrows(HallazgoNotFoundException.class, () ->
            cerrarUseCase.cerrar(id, "Motivo", LocalDate.now(), "user", "obs")
        );
    }

    @Test
    @DisplayName("ObtenerDashboardAuditoriaUseCase debe retornar la vista consolidada de métricas")
    void debeRetornarDashboardConsolidado() {
        DashboardAuditoriaView mockView = new DashboardAuditoriaView(
            10,
            4,
            3,
            2,
            1,
            List.of(new ConteoCategoria("Seguridad", 6), new ConteoCategoria("Infraestructura", 4)),
            List.of(new PromedioCategoria("Seguridad", 4.5))
        );

        when(hallazgoRepositoryPort.obtenerDashboard()).thenReturn(mockView);

        DashboardAuditoriaView view = dashboardUseCase.obtenerDashboard();
        assertNotNull(view);
        assertEquals(10, view.totalHallazgos());
        assertEquals(4, view.totalAbiertos());
        assertEquals(2, view.conteoPorCategoria().size());
        assertEquals(1, view.promedioDiasPorCategoria().size());
    }

    @Test
    @DisplayName("ConsultarHistorialUseCase debe retornar los eventos ordenados cronológicamente")
    void debeRetornarHistorialDelHallazgo() {
        HallazgoId id = HallazgoId.generar();
        List<CambioEstadoView> cambios = List.of(
            new CambioEstadoView(1L, id, null, EstadoHallazgo.ABIERTO, "admin", "Apertura", LocalDateTime.now().minusDays(2)),
            new CambioEstadoView(2L, id, EstadoHallazgo.ABIERTO, EstadoHallazgo.EN_REMEDIACION, "dev", "Plan", LocalDateTime.now().minusDays(1))
        );

        when(historialAuditoriaPort.consultarHistorialPorHallazgo(id)).thenReturn(cambios);

        List<CambioEstadoView> resultado = historialUseCase.consultarPorHallazgo(id);
        assertEquals(2, resultado.size());
        assertEquals(EstadoHallazgo.ABIERTO, resultado.get(0).estadoNuevo());
        assertEquals(EstadoHallazgo.EN_REMEDIACION, resultado.get(1).estadoNuevo());
    }
}
