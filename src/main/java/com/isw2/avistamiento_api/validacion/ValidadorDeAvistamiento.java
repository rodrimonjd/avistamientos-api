package com.isw2.avistamiento_api.validacion;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import com.isw2.avistamiento_api.model.Avistamiento;
import com.isw2.avistamiento_api.reloj.Reloj;

@Component
public class ValidadorDeAvistamiento {

    private final Reloj reloj;

    public ValidadorDeAvistamiento(Reloj reloj) {
        this.reloj = reloj;
    }

    public void validar(Avistamiento a) {
        validarEspecie(a.getEspecie());
        validarFecha(a.getFecha());
        validarNoVacio("lugar", a.getLugar());
        validarNoVacio("observador", a.getObservador());
    }

    private void validarEspecie(String especie) {
        if (especie == null) {
            throw new AvistamientoInvalidoException("especie: es obligatoria");
        }
        int largo = especie.trim().length();
        if (largo < 3 || largo > 50) {
            throw new AvistamientoInvalidoException("especie: debe tener entre 3 y 50 caracteres");
        }
    }

    private void validarFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new AvistamientoInvalidoException("fecha: es obligatoria");
        }
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new AvistamientoInvalidoException("fecha: debe tener el formato AAAA-MM-DD");
        }
        LocalDate fechaParseada;
        try {
            fechaParseada = LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new AvistamientoInvalidoException("fecha: no es una fecha real (formato AAAA-MM-DD)");
        }
        if (fechaParseada.isAfter(reloj.hoy())) {
            throw new AvistamientoInvalidoException("fecha: no puede ser posterior a hoy");
        }
    }

    private void validarNoVacio(String campo, String valor) {
        if (valor == null || valor.isBlank()) {
            throw new AvistamientoInvalidoException(campo + ": no puede estar vacío");
        }
    }
}