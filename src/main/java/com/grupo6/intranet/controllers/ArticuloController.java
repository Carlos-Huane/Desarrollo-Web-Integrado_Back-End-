package com.grupo6.intranet.controllers;

import com.grupo6.intranet.dtos.ArticuloRequest;
import com.grupo6.intranet.models.Articulo;
import com.grupo6.intranet.services.ArticuloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articulos")
@CrossOrigin(origins = "*")
public class ArticuloController {

    @Autowired
    private ArticuloService articuloService;

    @GetMapping
    public List<Articulo> listarActivos() {
        return articuloService.listarActivos();
    }

    @GetMapping("/todos")
    public List<Articulo> listarTodos() {
        return articuloService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Articulo> buscarPorId(@PathVariable Long id) {
        return articuloService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoriaId}")
    public List<Articulo> listarPorCategoria(@PathVariable Long categoriaId) {
        return articuloService.listarPorCategoria(categoriaId);
    }

    @GetMapping("/buscar")
    public List<Articulo> buscar(@RequestParam(required = false) String q) {
        return articuloService.buscar(q);
    }

    @GetMapping("/top")
    public List<Articulo> topVistos() {
        return articuloService.topVistos();
    }

    @PostMapping("/autor/{autorId}")
    public ResponseEntity<Articulo> crear(
            @Valid @RequestBody ArticuloRequest req,
            @PathVariable Long autorId) {
        return ResponseEntity.ok(articuloService.crear(req, autorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Articulo> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ArticuloRequest req) {
        return articuloService.actualizar(id, req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Articulo> activar(@PathVariable Long id) {
        return articuloService.activar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Articulo> desactivar(@PathVariable Long id) {
        return articuloService.desactivar(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
