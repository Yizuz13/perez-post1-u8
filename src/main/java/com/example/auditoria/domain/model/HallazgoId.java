package com.example.auditoria.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object inmutable que representa el identificador único fuertemente tipado
 * de un hallazgo de auditoría.
 */
public record HallazgoId(UUID valor) {

    public HallazgoId {
        Objects.requireNonNull(valor, "El valor del identificador de hallazgo no puede ser nulo");
    }

    public static HallazgoId generar() {
        return new HallazgoId(UUID.randomUUID());
    }

    public static HallazgoId desde(String id) {
        Objects.requireNonNull(id, "El identificador de hallazgo no puede ser nulo");
        try {
            return new HallazgoId(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El formato del UUID es inválido: " + id, e);
        }
    }

    public static HallazgoId desde(UUID id) {
        return new HallazgoId(id);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
