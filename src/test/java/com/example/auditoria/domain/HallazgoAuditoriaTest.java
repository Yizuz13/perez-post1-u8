package com.example.auditoria.domain;

import com.example.auditoria.domain.exception.TransicionInvalidaException;
import com.example.auditoria.domain.model.EstadoHallazgo;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.PlanRemediacion;
import com.example.auditoria.domain.model.Severidad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Pruebas Unitarias del Dominio: Agregado HallazgoAuditoria y Máquina de Estados")
class HallazgoAuditoriaTest {

    @Test
    @DisplayName("Debe registrar un hallazgo en estado inicial ABIERTO sin plan ni fecha de cierre")
    void debeRegistrarHallazgoEnEstadoAbierto() {
        HallazgoAuditoria hallazgo = HallazgoAuditoria.crearNuevo(
            "Falta de cifrado en reposo",
            "La base de datos de auditoría no cuenta con TDE habilitado",
            "Seguridad de la Información",
            Severidad.CRITICA,
            LocalDate.of(2026, 9, 15)
        );

        assertNotNull(hallazgo.getId());
        assertEquals("Falta de cifrado en reposo", hallazgo.getTitulo());
        assertEquals("Seguridad de la Información", hallazgo.getCategoria());
        assertEquals(Severidad.CRITICA, hallazgo.getSeveridad());
        assertEquals(EstadoHallazgo.ABIERTO, hallazgo.getEstado());
        assertEquals(LocalDate.of(2026, 9, 15), hallazgo.getFechaDeteccion());
        assertNull(hallazgo.getPlanRemediacion());
        assertNull(hallazgo.getFechaCierre());
        assertNull(hallazgo.getMotivoCierre());
    }

    @Test
    @DisplayName("Debe fallar al registrar un hallazgo con campos requeridos nulos o vacíos")
    void debeFallarAlCrearConDatosInvalidos() {
        assertThrows(IllegalArgumentException.class, () ->
            HallazgoAuditoria.crearNuevo("", "Desc", "Cat", Severidad.ALTA, LocalDate.now())
        );

        assertThrows(NullPointerException.class, () ->
            HallazgoAuditoria.crearNuevo("Título", "Desc", "Cat", null, LocalDate.now())
        );
    }

    @Test
    @DisplayName("Debe permitir la transición válida: ABIERTO -> EN_REMEDIACION con plan válido")
    void debeTransicionarDeAbiertoAEnRemediacion() {
        HallazgoAuditoria hallazgo = HallazgoAuditoria.crearNuevo(
            "Puertos inseguros expuestos",
            "El puerto 22 SSH se encuentra expuesto a Internet",
            "Infraestructura",
            Severidad.ALTA,
            LocalDate.of(2026, 9, 20)
        );

        PlanRemediacion plan = new PlanRemediacion(
            "Cerrar puerto en security group y habilitar túnel VPN",
            "Ing. Carlos Mendoza",
            LocalDate.of(2026, 10, 5)
        );

        hallazgo.iniciarRemediacion(plan);

        assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());
        assertNotNull(hallazgo.getPlanRemediacion());
        assertEquals("Ing. Carlos Mendoza", hallazgo.getPlanRemediacion().responsable());
    }

    @Test
    @DisplayName("Debe rechazar la transición inválida: ABIERTO -> CERRADO (debe pasar por remediación)")
    void debeRechazarTransicionInvalidaDeAbiertoACerrado() {
        HallazgoAuditoria hallazgo = HallazgoAuditoria.crearNuevo(
            "Violación de política de contraseñas",
            "Contraseñas sin rotación en 90 días",
            "Gobierno TI",
            Severidad.MEDIA,
            LocalDate.now()
        );

        TransicionInvalidaException ex = assertThrows(TransicionInvalidaException.class, () ->
            hallazgo.cerrar("Se cerró directamente sin remediación", LocalDate.now())
        );

        assertEquals(EstadoHallazgo.ABIERTO, ex.getEstadoOrigen());
        assertEquals(EstadoHallazgo.CERRADO, ex.getEstadoDestino());
    }

    @Test
    @DisplayName("Debe permitir el ciclo completo: ABIERTO -> EN_REMEDIACION -> CERRADO -> REABIERTO -> EN_REMEDIACION")
    void debeEjecutarCicloDeVidaCompletoPermitido() {
        LocalDate fechaDeteccion = LocalDate.of(2026, 9, 1);
        HallazgoAuditoria hallazgo = HallazgoAuditoria.crearNuevo(
            "Backups sin prueba de restauración",
            "No se han efectuado pruebas trimestrales de restore",
            "Continuidad de Negocio",
            Severidad.ALTA,
            fechaDeteccion
        );

        // 1. Iniciar remediación
        PlanRemediacion plan1 = new PlanRemediacion(
            "Ejecutar simulacro de restore en ambiente de pruebas",
            "Ing. Sofia Ramirez",
            LocalDate.of(2026, 9, 10)
        );
        hallazgo.iniciarRemediacion(plan1);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());

        // 2. Cerrar hallazgo
        LocalDate fechaCierre = LocalDate.of(2026, 9, 12);
        hallazgo.cerrar("Simulacro ejecutado exitosamente con RTO de 45 minutos", fechaCierre);
        assertEquals(EstadoHallazgo.CERRADO, hallazgo.getEstado());
        assertEquals(fechaCierre, hallazgo.getFechaCierre());

        // 3. Reabrir hallazgo
        hallazgo.reabrir("En la revisión externa se identificó que no se incluyó la base de datos transaccional");
        assertEquals(EstadoHallazgo.REABIERTO, hallazgo.getEstado());
        assertNull(hallazgo.getFechaCierre());

        // 4. Volver a iniciar remediación desde REABIERTO
        PlanRemediacion plan2 = new PlanRemediacion(
            "Ampliar alcance del simulacro a réplicas transaccionales",
            "Ing. Jesus Perez",
            LocalDate.of(2026, 9, 25)
        );
        hallazgo.iniciarRemediacion(plan2);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, hallazgo.getEstado());
    }

    @Test
    @DisplayName("Debe fallar al intentar cerrar un hallazgo con fecha anterior a la de detección")
    void debeFallarAlCerrarConFechaAnteriorADeteccion() {
        HallazgoAuditoria hallazgo = HallazgoAuditoria.crearNuevo(
            "Vulnerabilidad en componente frontend",
            "Librería desactualizada con CVE crítico",
            "Desarrollo Seguro",
            Severidad.CRITICA,
            LocalDate.of(2026, 9, 20)
        );

        hallazgo.iniciarRemediacion(new PlanRemediacion("Actualizar paquete npm", "DevOps", LocalDate.of(2026, 9, 22)));

        assertThrows(IllegalArgumentException.class, () ->
            hallazgo.cerrar("Parche aplicado", LocalDate.of(2026, 9, 10))
        );
    }

    @Test
    @DisplayName("Debe validar la inmutabilidad y reglas del Value Object PlanRemediacion")
    void debeValidarInvariantesDePlanRemediacion() {
        assertThrows(IllegalArgumentException.class, () ->
            new PlanRemediacion("", "Responsable", LocalDate.now())
        );

        assertThrows(IllegalArgumentException.class, () ->
            new PlanRemediacion("Acción", "   ", LocalDate.now())
        );

        assertThrows(NullPointerException.class, () ->
            new PlanRemediacion("Acción", "Responsable", null)
        );
    }
}
