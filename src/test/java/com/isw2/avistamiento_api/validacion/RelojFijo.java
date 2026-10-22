package com.isw2.avistamiento_api.validacion;

import java.time.LocalDate;

import com.isw2.avistamiento_api.reloj.Reloj;

public class RelojFijo implements Reloj {

    private final LocalDate hoy;

    public RelojFijo(LocalDate hoy) {
        this.hoy = hoy;
    }

    @Override
    public LocalDate hoy() {
        return hoy;
    }
}