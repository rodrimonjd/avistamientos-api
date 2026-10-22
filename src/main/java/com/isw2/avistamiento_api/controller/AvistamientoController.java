package com.isw2.avistamiento_api.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isw2.avistamiento_api.model.Avistamiento;
import com.isw2.avistamiento_api.repository.AvistamientoRepository;
import com.isw2.avistamiento_api.validacion.AvistamientoInvalidoException;
import com.isw2.avistamiento_api.validacion.ValidadorDeAvistamiento;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repo;
    private final ValidadorDeAvistamiento validador;

    public AvistamientoController(AvistamientoRepository repo, ValidadorDeAvistamiento validador) {
        this.repo = repo;
        this.validador = validador;
    }

    // listar todo
    @GetMapping
    public List<Avistamiento> listar() {
        return repo.findAll();
    }

    // ver por id
    @GetMapping("/{id}")
    public ResponseEntity<Avistamiento> ver(@PathVariable Long id) {
        Optional<Avistamiento> avistamiento = repo.findById(id);
        if (avistamiento.isPresent()) {
            return ResponseEntity.ok(avistamiento.get());
        }
        return ResponseEntity.notFound().build();
    }

    // crear
    @PostMapping
    public ResponseEntity<Avistamiento> crear(@RequestBody Avistamiento nuevo) {
        validador.validar(nuevo);
        nuevo.setId(null);
        Avistamiento guardado = repo.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @RequestBody Avistamiento datos) {
        validador.validar(datos);
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datos.setId(id);
        Avistamiento actualizado = repo.save(datos);
        return ResponseEntity.ok(actualizado);
    }

    // eliminar por id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // validación fallida -> 400 con el campo que falló
    @ExceptionHandler(AvistamientoInvalidoException.class)
    public ResponseEntity<Map<String, String>> invalido(AvistamientoInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}