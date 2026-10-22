package com.isw2.avistamiento_api.validacion;

public class AvistamientoInvalidoException extends RuntimeException {
    public AvistamientoInvalidoException(String mensaje) {
        super(mensaje);
    }
}