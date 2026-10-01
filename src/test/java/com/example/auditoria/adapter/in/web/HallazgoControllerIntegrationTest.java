package com.example.auditoria.adapter.in.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Pruebas de Integración de la API REST (HallazgoController, GlobalExceptionHandler y DTOs)")
class HallazgoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.example.auditoria.adapter.out.persistence.HallazgoJpaRepository hallazgoJpaRepository;

    @Autowired
    private com.example.auditoria.adapter.out.persistence.HistorialCambioEstadoJpaRepository historialRepository;

    @org.junit.jupiter.api.BeforeEach
    void cleanDatabase() {
        historialRepository.deleteAll();
        hallazgoJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Flujo End-to-End: Registrar, Remediación, Cierre, Reapertura, Historial y Dashboard")
    void flujoCompletoDeCicloDeVidaYConsultas() throws Exception {
        // 1. Registrar Hallazgo
        String registroJson = """
            {
                "titulo": "Credenciales hardcodeadas en código fuente",
                "descripcion": "Se identificaron claves de acceso de AWS en archivo de configuración",
                "categoria": "Seguridad Aplicativa",
                "severidad": "CRITICA",
                "fechaDeteccion": "2026-09-10",
                "usuarioResponsable": "auditor_perez",
                "observacion": "Hallazgo detectado mediante análisis SAST"
            }
            """;

        MvcResult createResult = mockMvc.perform(post("/api/hallazgos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registroJson))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.titulo", is("Credenciales hardcodeadas en código fuente")))
            .andExpect(jsonPath("$.estado", is("ABIERTO")))
            .andExpect(jsonPath("$.severidad", is("CRITICA")))
            .andReturn();

        JsonNode jsonNode = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String hallazgoId = jsonNode.get("id").asText();

        // 2. Consultar por ID
        mockMvc.perform(get("/api/hallazgos/{id}", hallazgoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(hallazgoId)))
            .andExpect(jsonPath("$.estado", is("ABIERTO")));

        // 3. Iniciar Remediación
        String remediacionJson = """
            {
                "descripcionAccion": "Rotar credenciales de AWS e implementar AWS Secrets Manager",
                "responsable": "Ing. Jesus Perez",
                "fechaCompromiso": "2026-09-15",
                "usuarioResponsable": "lider_devops",
                "observacion": "Acción prioritaria por riesgo crítico"
            }
            """;

        mockMvc.perform(post("/api/hallazgos/{id}/iniciar-remediacion", hallazgoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(remediacionJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado", is("EN_REMEDIACION")))
            .andExpect(jsonPath("$.planRemediacion.responsable", is("Ing. Jesus Perez")));

        // 4. Cerrar Hallazgo
        String cerrarJson = """
            {
                "motivoCierre": "Secretos migrados satisfactoriamente a Vault y Secrets Manager",
                "fechaCierre": "2026-09-14",
                "usuarioResponsable": "auditor_perez",
                "observacion": "Verificación de escaneo DAST y SAST limpia"
            }
            """;

        mockMvc.perform(post("/api/hallazgos/{id}/cerrar", hallazgoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cerrarJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado", is("CERRADO")))
            .andExpect(jsonPath("$.fechaCierre", is("2026-09-14")));

        // 5. Reabrir Hallazgo
        String reabrirJson = """
            {
                "justificacionReapertura": "Persiste una variable de entorno desprotegida en el runner de CI/CD",
                "usuarioResponsable": "qa_auditor",
                "observacion": "Revisión posterior de pipelines"
            }
            """;

        mockMvc.perform(post("/api/hallazgos/{id}/reabrir", hallazgoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reabrirJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado", is("REABIERTO")))
            .andExpect(jsonPath("$.justificacionReapertura", containsString("runner de CI/CD")));

        // 6. Consultar Historial (Bitácora Append-Only)
        mockMvc.perform(get("/api/hallazgos/{id}/historial", hallazgoId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4))) // Registro inicial, Remediación, Cierre, Reapertura
            .andExpect(jsonPath("$[0].estadoNuevo", is("ABIERTO")))
            .andExpect(jsonPath("$[1].estadoNuevo", is("EN_REMEDIACION")))
            .andExpect(jsonPath("$[2].estadoNuevo", is("CERRADO")))
            .andExpect(jsonPath("$[3].estadoNuevo", is("REABIERTO")));

        // 7. Consultar Dashboard
        mockMvc.perform(get("/api/hallazgos/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalHallazgos", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.totalReabiertos", greaterThanOrEqualTo(1)))
            .andExpect(jsonPath("$.conteoPorCategoria", notNullValue()));
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request cuando se intenta una transición prohibida")
    void debeRetornar400EnTransicionInvalida() throws Exception {
        // Registrar hallazgo en ABIERTO
        String registroJson = """
            {
                "titulo": "Puerto Telnet abierto",
                "descripcion": "El puerto 23 se encuentra habilitado",
                "categoria": "Redes",
                "severidad": "MEDIA",
                "fechaDeteccion": "2026-09-20"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/hallazgos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registroJson))
            .andExpect(status().isCreated())
            .andReturn();

        String id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asText();

        // Intentar cerrar directamente sin pasar por EN_REMEDIACION
        String cerrarInvalidoJson = """
            {
                "motivoCierre": "Cierre directo forzado",
                "fechaCierre": "2026-09-21"
            }
            """;

        mockMvc.perform(post("/api/hallazgos/{id}/cerrar", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cerrarInvalidoJson))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Transición Inválida")))
            .andExpect(jsonPath("$.mensaje", containsString("no es posible transicionar de ABIERTO a CERRADO")));
    }

    @Test
    @DisplayName("Debe retornar 404 Not Found cuando se consulta un hallazgo inexistente")
    void debeRetornar404CuandoNoExiste() throws Exception {
        UUID randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/hallazgos/{id}", randomId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status", is(404)))
            .andExpect(jsonPath("$.error", is("Recurso No Encontrado")))
            .andExpect(jsonPath("$.mensaje", containsString(randomId.toString())));
    }

    @Test
    @DisplayName("Debe retornar 400 Bad Request cuando el payload falla las validaciones Bean Validation")
    void debeRetornar400EnValidacionInvalida() throws Exception {
        String jsonInvalido = """
            {
                "titulo": "",
                "descripcion": "",
                "categoria": "",
                "severidad": null
            }
            """;

        mockMvc.perform(post("/api/hallazgos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonInvalido))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is(400)))
            .andExpect(jsonPath("$.error", is("Error de Validación")));
    }
}
