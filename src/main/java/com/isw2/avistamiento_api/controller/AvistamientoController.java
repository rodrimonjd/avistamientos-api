package com.isw2.avistamiento_api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isw2.avistamiento_api.model.Avistamiento;
import com.isw2.avistamiento_api.repository.AvistamientoRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/avistamientos")
public class AvistamientoController {

    private final AvistamientoRepository repo;

    public AvistamientoController(AvistamientoRepository repo) {
        this.repo = repo;
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
    public ResponseEntity<Avistamiento> crear(@Valid @RequestBody Avistamiento nuevo) {
        nuevo.setId(null);
        Avistamiento guardado = repo.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
    
    // actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Avistamiento> actualizar(@PathVariable Long id, @Valid @RequestBody Avistamiento datos) {
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
}
