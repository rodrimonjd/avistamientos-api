package com.isw2.avistamiento_api.validacion;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.isw2.avistamiento_api.model.Avistamiento;

class ValidadorDeAvistamientoTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 21);

    private ValidadorDeAvistamiento validador;

    @BeforeEach
    void preparar() {
        validador = new ValidadorDeAvistamiento(new RelojFijo(HOY));
    }

    // ---------- helpers ----------

    private Avistamiento conEspecie(String especie) {
        return new Avistamiento(especie, "Parque Urbano", "2026-10-21", "Juan");
    }

    private Avistamiento conFecha(String fecha) {
        return new Avistamiento("Colibrí", "Parque Urbano", fecha, "Juan");
    }

    private void assertInvalidoPorCampo(Avistamiento a, String campo) {
        AvistamientoInvalidoException ex =
                assertThrows(AvistamientoInvalidoException.class, () -> validador.validar(a));
        assertTrue(ex.getMessage().startsWith(campo),
                "El mensaje debería empezar con '" + campo + "' pero fue: " + ex.getMessage());
    }

    // ---------- especie ----------

    @Test
    void avistamientoCompletoYValido_noLanzaExcepcion() {
        Avistamiento a = conEspecie("Colibrí");

        assertDoesNotThrow(() -> validador.validar(a));
    }

    @Test
    void especieDeDosCaracteres_seRechazaIndicandoEspecie() {
        Avistamiento a = conEspecie("ab");

        assertInvalidoPorCampo(a, "especie");
    }

    @Test
    void especieDeTresCaracteres_seAcepta() {
        Avistamiento a = conEspecie("abc");

        assertDoesNotThrow(() -> validador.validar(a));
    }

    @Test
    void especieDeCincuentaCaracteres_seAcepta() {
        Avistamiento a = conEspecie("a".repeat(50));

        assertDoesNotThrow(() -> validador.validar(a));
    }

    @Test
    void especieDeCincuentaYUnCaracteres_seRechazaIndicandoEspecie() {
        Avistamiento a = conEspecie("a".repeat(51));

        assertInvalidoPorCampo(a, "especie");
    }

    @Test
    void especieNula_seRechazaIndicandoEspecie() {
        Avistamiento a = conEspecie(null);

        assertInvalidoPorCampo(a, "especie");
    }

    // ---------- fecha (usa el stub RelojFijo) ----------

    @Test
    void fechaDeHoy_seAcepta() {
        Avistamiento a = conFecha("2026-10-21");

        assertDoesNotThrow(() -> validador.validar(a));
    }

    @Test
    void fechaDeManana_seRechazaIndicandoFecha() {
        Avistamiento a = conFecha("2026-10-22");

        assertInvalidoPorCampo(a, "fecha");
    }

    @Test
    void fechaConFormatoDiaMesAnio_seRechazaIndicandoFecha() {
        Avistamiento a = conFecha("21/10/2026");

        assertInvalidoPorCampo(a, "fecha");
    }

    @Test
    void fechaConFormatoCorrectoPeroImposible_seRechazaIndicandoFecha() {
        Avistamiento a = conFecha("2026-02-30");

        assertInvalidoPorCampo(a, "fecha");
    }

    @Test
    void fechaNula_seRechazaIndicandoFecha() {
        Avistamiento a = conFecha(null);

        assertInvalidoPorCampo(a, "fecha");
    }

    // ---------- lugar y observador ----------

    @Test
    void lugarVacio_seRechazaIndicandoLugar() {
        Avistamiento a = new Avistamiento("Colibrí", "", "2026-10-21", "Juan");

        assertInvalidoPorCampo(a, "lugar");
    }

    @Test
    void observadorSoloConEspacios_seRechazaIndicandoObservador() {
        Avistamiento a = new Avistamiento("Colibrí", "Parque Urbano", "2026-10-21", "   ");

        assertInvalidoPorCampo(a, "observador");
    }
}