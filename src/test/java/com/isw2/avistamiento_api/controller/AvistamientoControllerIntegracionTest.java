package com.isw2.avistamiento_api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.isw2.avistamiento_api.model.Avistamiento;
import com.isw2.avistamiento_api.repository.AvistamientoRepository;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AvistamientoControllerIntegracionTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AvistamientoRepository repo;

    @BeforeEach
    void limpiarBaseDeDatos() {
        repo.deleteAll();
    }

    // ---------- helpers ----------

    private Avistamiento sembrar(String especie, String lugar, String fecha, String observador) {
        return repo.save(new Avistamiento(especie, lugar, fecha, observador));
    }

    private static String json(String especie, String lugar, String fecha, String observador) {
        return String.format(
                "{\"especie\":\"%s\",\"lugar\":\"%s\",\"fecha\":\"%s\",\"observador\":\"%s\"}",
                especie, lugar, fecha, observador);
    }

    // ---------- GET /avistamientos ----------

    @Test
    void listar_conDosAvistamientosGuardados_responde200ConAmbos() throws Exception {
        sembrar("Colibrí", "Parque Urbano", "2026-09-29", "Juan");
        sembrar("Cóndor", "Valle de la Luna", "2026-10-01", "Ana");

        mvc.perform(get("/avistamientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].especie").value("Colibrí"))
                .andExpect(jsonPath("$[1].especie").value("Cóndor"));
    }

    // ---------- GET /avistamientos/{id} ----------

    @Test
    void ver_conIdExistente_responde200ConTodosLosCampos() throws Exception {
        Avistamiento guardado = sembrar("Colibrí", "Parque Urbano", "2026-09-29", "Juan");

        mvc.perform(get("/avistamientos/" + guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.especie").value("Colibrí"))
                .andExpect(jsonPath("$.lugar").value("Parque Urbano"))
                .andExpect(jsonPath("$.fecha").value("2026-09-29"))
                .andExpect(jsonPath("$.observador").value("Juan"));
    }

    @Test
    void ver_conIdInexistente_responde404() throws Exception {
        mvc.perform(get("/avistamientos/9999"))
                .andExpect(status().isNotFound());
    }

    // ---------- POST /avistamientos ----------

    @Test
    void crear_conDatosValidos_responde201YElAvistamientoQuedaGuardado() throws Exception {
        String cuerpo = mvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Colibrí", "Parque Urbano", "2026-09-29", "Juan")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.especie").value("Colibrí"))
                .andExpect(jsonPath("$.lugar").value("Parque Urbano"))
                .andExpect(jsonPath("$.fecha").value("2026-09-29"))
                .andExpect(jsonPath("$.observador").value("Juan"))
                .andReturn().getResponse().getContentAsString();

        Number id = JsonPath.read(cuerpo, "$.id");

        // efecto: el nuevo id se puede consultar
        mvc.perform(get("/avistamientos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.especie").value("Colibrí"));
    }

    @Test
    void crear_conEspecieDeDosCaracteres_responde400IndicandoEspecieYNoGuarda() throws Exception {
        mvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("ab", "Parque Urbano", "2026-09-29", "Juan")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("especie: debe tener entre 3 y 50 caracteres"));

        assertEquals(0, repo.count());
    }

    @Test
    void crear_conFechaEnFormatoInvalido_responde400IndicandoFecha() throws Exception {
        mvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Colibrí", "Parque Urbano", "29/09/2026", "Juan")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("fecha: debe tener el formato AAAA-MM-DD"));
    }

    @Test
    void crear_conFechaFutura_responde400IndicandoFecha() throws Exception {
        mvc.perform(post("/avistamientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Colibrí", "Parque Urbano", "2999-01-01", "Juan")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("fecha: no puede ser posterior a hoy"));
    }

    // ---------- PUT /avistamientos/{id} ----------

    @Test
    void actualizar_conDatosValidos_responde200YCambiaElRegistro() throws Exception {
        Avistamiento guardado = sembrar("Colibrí", "Parque Urbano", "2026-09-29", "Juan");

        mvc.perform(put("/avistamientos/" + guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Cóndor andino", "Valle de la Luna", "2026-10-01", "Ana")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(guardado.getId()))
                .andExpect(jsonPath("$.especie").value("Cóndor andino"));

        // efecto: el GET devuelve los datos nuevos
        mvc.perform(get("/avistamientos/" + guardado.getId()))
                .andExpect(jsonPath("$.especie").value("Cóndor andino"))
                .andExpect(jsonPath("$.lugar").value("Valle de la Luna"))
                .andExpect(jsonPath("$.observador").value("Ana"));
    }

    @Test
    void actualizar_conLugarVacio_responde400IndicandoLugarYNoCambiaElRegistro() throws Exception {
        Avistamiento guardado = sembrar("Colibrí", "Parque Urbano", "2026-09-29", "Juan");

        mvc.perform(put("/avistamientos/" + guardado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Cóndor andino", "", "2026-10-01", "Ana")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("lugar: no puede estar vacío"));

        mvc.perform(get("/avistamientos/" + guardado.getId()))
                .andExpect(jsonPath("$.especie").value("Colibrí"));
    }

    @Test
    void actualizar_conIdInexistente_responde404() throws Exception {
        mvc.perform(put("/avistamientos/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Colibrí", "Parque Urbano", "2026-09-29", "Juan")))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE /avistamientos/{id} ----------

    @Test
    void eliminar_conIdExistente_responde204YLuegoElGetDa404() throws Exception {
        Avistamiento guardado = sembrar("Colibrí", "Parque Urbano", "2026-09-29", "Juan");

        mvc.perform(delete("/avistamientos/" + guardado.getId()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/avistamientos/" + guardado.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminar_conIdInexistente_responde404() throws Exception {
        mvc.perform(delete("/avistamientos/9999"))
                .andExpect(status().isNotFound());
    }
}