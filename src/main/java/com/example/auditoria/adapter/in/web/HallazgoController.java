package com.example.auditoria.adapter.in.web;

import com.example.auditoria.adapter.in.web.dto.CambioEstadoResponse;
import com.example.auditoria.adapter.in.web.dto.CerrarRequest;
import com.example.auditoria.adapter.in.web.dto.DashboardResponse;
import com.example.auditoria.adapter.in.web.dto.HallazgoResponse;
import com.example.auditoria.adapter.in.web.dto.IniciarRemediacionRequest;
import com.example.auditoria.adapter.in.web.dto.ReabrirRequest;
import com.example.auditoria.adapter.in.web.dto.RegistrarHallazgoRequest;
import com.example.auditoria.domain.model.HallazgoAuditoria;
import com.example.auditoria.domain.model.HallazgoId;
import com.example.auditoria.domain.model.PlanRemediacion;
import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.port.out.CambioEstadoView;
import com.example.auditoria.usecase.port.out.DashboardAuditoriaView;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST que actúa como adaptador de entrada (Inbound Adapter)
 * para la gestión del ciclo de vida de hallazgos de auditoría.
 */
@RestController
@RequestMapping("/api/hallazgos")
public class HallazgoController {

    private final RegistrarHallazgoUseCase registrarHallazgoUseCase;
    private final IniciarRemediacionUseCase iniciarRemediacionUseCase;
    private final CerrarHallazgoUseCase cerrarHallazgoUseCase;
    private final ReabrirHallazgoUseCase reabrirHallazgoUseCase;
    private final ConsultarHallazgoUseCase consultarHallazgoUseCase;
    private final ObtenerDashboardAuditoriaUseCase obtenerDashboardAuditoriaUseCase;
    private final ConsultarHistorialUseCase consultarHistorialUseCase;

    public HallazgoController(
        RegistrarHallazgoUseCase registrarHallazgoUseCase,
        IniciarRemediacionUseCase iniciarRemediacionUseCase,
        CerrarHallazgoUseCase cerrarHallazgoUseCase,
        ReabrirHallazgoUseCase reabrirHallazgoUseCase,
        ConsultarHallazgoUseCase consultarHallazgoUseCase,
        ObtenerDashboardAuditoriaUseCase obtenerDashboardAuditoriaUseCase,
        ConsultarHistorialUseCase consultarHistorialUseCase
    ) {
        this.registrarHallazgoUseCase = registrarHallazgoUseCase;
        this.iniciarRemediacionUseCase = iniciarRemediacionUseCase;
        this.cerrarHallazgoUseCase = cerrarHallazgoUseCase;
        this.reabrirHallazgoUseCase = reabrirHallazgoUseCase;
        this.consultarHallazgoUseCase = consultarHallazgoUseCase;
        this.obtenerDashboardAuditoriaUseCase = obtenerDashboardAuditoriaUseCase;
        this.consultarHistorialUseCase = consultarHistorialUseCase;
    }

    /**
     * Endpoint 1: Registrar un nuevo hallazgo de auditoría en estado ABIERTO.
     */
    @PostMapping
    public ResponseEntity<HallazgoResponse> registrarHallazgo(
        @Valid @RequestBody RegistrarHallazgoRequest request
    ) {
        HallazgoAuditoria guardado = registrarHallazgoUseCase.registrar(
            request.titulo(),
            request.descripcion(),
            request.categoria(),
            request.severidad(),
            request.fechaDeteccion(),
            request.usuarioResponsable(),
            request.observacion()
        );

        HallazgoResponse response = HallazgoResponse.fromDomain(guardado);
        URI location = URI.create("/api/hallazgos/" + guardado.getId().valor());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Endpoint 2: Listar todos los hallazgos registrados.
     */
    @GetMapping
    public ResponseEntity<List<HallazgoResponse>> listarTodos() {
        List<HallazgoAuditoria> hallazgos = consultarHallazgoUseCase.listarTodos();
        List<HallazgoResponse> responses = hallazgos.stream()
            .map(HallazgoResponse::fromDomain)
            .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint 3: Consultar métricas del Dashboard de auditoría (CQRS liviano).
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> obtenerDashboard() {
        DashboardAuditoriaView view = obtenerDashboardAuditoriaUseCase.obtenerDashboard();
        return ResponseEntity.ok(DashboardResponse.fromView(view));
    }

    /**
     * Endpoint 4: Consultar el detalle de un hallazgo por su identificador UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<HallazgoResponse> consultarPorId(@PathVariable UUID id) {
        HallazgoAuditoria hallazgo = consultarHallazgoUseCase.consultarPorId(HallazgoId.desde(id));
        return ResponseEntity.ok(HallazgoResponse.fromDomain(hallazgo));
    }

    /**
     * Endpoint 5: Consultar la trazabilidad histórica de cambios de estado (bitácora append-only).
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<CambioEstadoResponse>> consultarHistorial(@PathVariable UUID id) {
        List<CambioEstadoView> historial = consultarHistorialUseCase.consultarPorHallazgo(HallazgoId.desde(id));
        List<CambioEstadoResponse> responses = historial.stream()
            .map(CambioEstadoResponse::fromView)
            .toList();
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint 6: Iniciar remediación (transición a EN_REMEDIACION).
     * Soporta tanto POST como PATCH para adaptabilidad a clientes REST.
     */
    @PostMapping("/{id}/iniciar-remediacion")
    public ResponseEntity<HallazgoResponse> iniciarRemediacionPost(
        @PathVariable UUID id,
        @Valid @RequestBody IniciarRemediacionRequest request
    ) {
        return ejecutarIniciarRemediacion(id, request);
    }

    @PatchMapping("/{id}/iniciar-remediacion")
    public ResponseEntity<HallazgoResponse> iniciarRemediacionPatch(
        @PathVariable UUID id,
        @Valid @RequestBody IniciarRemediacionRequest request
    ) {
        return ejecutarIniciarRemediacion(id, request);
    }

    private ResponseEntity<HallazgoResponse> ejecutarIniciarRemediacion(UUID id, IniciarRemediacionRequest request) {
        PlanRemediacion plan = new PlanRemediacion(
            request.descripcionAccion(),
            request.responsable(),
            request.fechaCompromiso()
        );

        HallazgoAuditoria actualizado = iniciarRemediacionUseCase.iniciarRemediacion(
            HallazgoId.desde(id),
            plan,
            request.usuarioResponsable(),
            request.observacion()
        );

        return ResponseEntity.ok(HallazgoResponse.fromDomain(actualizado));
    }

    /**
     * Endpoint 7: Cerrar hallazgo (transición a CERRADO).
     * Soporta tanto POST como PATCH.
     */
    @PostMapping("/{id}/cerrar")
    public ResponseEntity<HallazgoResponse> cerrarHallazgoPost(
        @PathVariable UUID id,
        @Valid @RequestBody CerrarRequest request
    ) {
        return ejecutarCerrar(id, request);
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<HallazgoResponse> cerrarHallazgoPatch(
        @PathVariable UUID id,
        @Valid @RequestBody CerrarRequest request
    ) {
        return ejecutarCerrar(id, request);
    }

    private ResponseEntity<HallazgoResponse> ejecutarCerrar(UUID id, CerrarRequest request) {
        HallazgoAuditoria actualizado = cerrarHallazgoUseCase.cerrar(
            HallazgoId.desde(id),
            request.motivoCierre(),
            request.fechaCierre(),
            request.usuarioResponsable(),
            request.observacion()
        );

        return ResponseEntity.ok(HallazgoResponse.fromDomain(actualizado));
    }

    /**
     * Endpoint 8: Reabrir hallazgo (transición a REABIERTO).
     * Soporta tanto POST como PATCH.
     */
    @PostMapping("/{id}/reabrir")
    public ResponseEntity<HallazgoResponse> reabrirHallazgoPost(
        @PathVariable UUID id,
        @Valid @RequestBody ReabrirRequest request
    ) {
        return ejecutarReabrir(id, request);
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<HallazgoResponse> reabrirHallazgoPatch(
        @PathVariable UUID id,
        @Valid @RequestBody ReabrirRequest request
    ) {
        return ejecutarReabrir(id, request);
    }

    private ResponseEntity<HallazgoResponse> ejecutarReabrir(UUID id, ReabrirRequest request) {
        HallazgoAuditoria actualizado = reabrirHallazgoUseCase.reabrir(
            HallazgoId.desde(id),
            request.justificacionReapertura(),
            request.usuarioResponsable(),
            request.observacion()
        );

        return ResponseEntity.ok(HallazgoResponse.fromDomain(actualizado));
    }
}
