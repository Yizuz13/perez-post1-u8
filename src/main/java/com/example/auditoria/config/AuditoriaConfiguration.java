package com.example.auditoria.config;

import com.example.auditoria.usecase.CerrarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.ConsultarHistorialUseCase;
import com.example.auditoria.usecase.IniciarRemediacionUseCase;
import com.example.auditoria.usecase.ObtenerDashboardAuditoriaUseCase;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.RegistrarHallazgoUseCase;
import com.example.auditoria.usecase.impl.CerrarHallazgoUseCaseImpl;
import com.example.auditoria.usecase.impl.ConsultarHallazgoUseCaseImpl;
import com.example.auditoria.usecase.impl.ConsultarHistorialUseCaseImpl;
import com.example.auditoria.usecase.impl.IniciarRemediacionUseCaseImpl;
import com.example.auditoria.usecase.impl.ObtenerDashboardAuditoriaUseCaseImpl;
import com.example.auditoria.usecase.impl.ReabrirHallazgoUseCaseImpl;
import com.example.auditoria.usecase.impl.RegistrarHallazgoUseCaseImpl;
import com.example.auditoria.usecase.port.out.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.out.HistorialAuditoriaPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Spring para la inyección de dependencias de los casos de uso.
 * Registra manualmente cada caso de uso como @Bean en el contenedor de Spring,
 * garantizando que las clases en com.example.auditoria.usecase.impl permanezcan
 * 100% puras y sin acoplamiento a frameworks.
 */
@Configuration
public class AuditoriaConfiguration {

    @Bean
    public RegistrarHallazgoUseCase registrarHallazgoUseCase(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        return new RegistrarHallazgoUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
    }

    @Bean
    public IniciarRemediacionUseCase iniciarRemediacionUseCase(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        return new IniciarRemediacionUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
    }

    @Bean
    public CerrarHallazgoUseCase cerrarHallazgoUseCase(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        return new CerrarHallazgoUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
    }

    @Bean
    public ReabrirHallazgoUseCase reabrirHallazgoUseCase(
        HallazgoRepositoryPort hallazgoRepositoryPort,
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        return new ReabrirHallazgoUseCaseImpl(hallazgoRepositoryPort, historialAuditoriaPort);
    }

    @Bean
    public ConsultarHallazgoUseCase consultarHallazgoUseCase(
        HallazgoRepositoryPort hallazgoRepositoryPort
    ) {
        return new ConsultarHallazgoUseCaseImpl(hallazgoRepositoryPort);
    }

    @Bean
    public ObtenerDashboardAuditoriaUseCase obtenerDashboardAuditoriaUseCase(
        HallazgoRepositoryPort hallazgoRepositoryPort
    ) {
        return new ObtenerDashboardAuditoriaUseCaseImpl(hallazgoRepositoryPort);
    }

    @Bean
    public ConsultarHistorialUseCase consultarHistorialUseCase(
        HistorialAuditoriaPort historialAuditoriaPort
    ) {
        return new ConsultarHistorialUseCaseImpl(historialAuditoriaPort);
    }
}
