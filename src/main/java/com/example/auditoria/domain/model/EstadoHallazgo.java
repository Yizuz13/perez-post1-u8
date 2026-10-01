package com.example.auditoria.domain.model;

import com.example.auditoria.domain.exception.TransicionInvalidaException;

/**
 * Máquina de estados finita del ciclo de vida de un hallazgo de auditoría.
 * Flujo estricto: ABIERTO -> EN_REMEDIACION -> CERRADO -> REABIERTO -> EN_REMEDIACION
 */
public enum EstadoHallazgo {

    ABIERTO {
        @Override
        public boolean puedeTransicionarA(EstadoHallazgo destino) {
            return destino == EN_REMEDIACION;
        }
    },

    EN_REMEDIACION {
        @Override
        public boolean puedeTransicionarA(EstadoHallazgo destino) {
            return destino == CERRADO;
        }
    },

    CERRADO {
        @Override
        public boolean puedeTransicionarA(EstadoHallazgo destino) {
            return destino == REABIERTO;
        }
    },

    REABIERTO {
        @Override
        public boolean puedeTransicionarA(EstadoHallazgo destino) {
            return destino == EN_REMEDIACION;
        }
    };

    /**
     * Evalúa si una transición hacia el estado de destino es admisible.
     *
     * @param destino Estado al cual se desea transicionar.
     * @return true si la transición está permitida por las reglas de negocio, false en caso contrario.
     */
    public abstract boolean puedeTransicionarA(EstadoHallazgo destino);

    /**
     * Valida la transición lanzando una excepción de dominio si no es válida.
     *
     * @param destino Estado objetivo.
     * @throws TransicionInvalidaException si la transición viola la máquina de estados.
     */
    public void validarTransicion(EstadoHallazgo destino) {
        if (!puedeTransicionarA(destino)) {
            throw new TransicionInvalidaException(this, destino);
        }
    }
}
