package com.example.auditoria.domain.exception;

import com.example.auditoria.domain.model.HallazgoId;

/**
 * Excepción de dominio lanzada cuando una operación requiere la existencia
 * de un hallazgo pero este no es localizado en el sistema.
 */
public class HallazgoNotFoundException extends RuntimeException {

    public HallazgoNotFoundException(HallazgoId id) {
        super(String.format("No se encontró el hallazgo de auditoría con ID: %s", id.valor()));
    }

    public HallazgoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
