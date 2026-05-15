package com.grupo6.intranet.controllers;

import com.grupo6.intranet.models.Categoria;
import com.grupo6.intranet.models.Subcategoria;
import com.grupo6.intranet.services.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaService.listarActivas();
    }

    @GetMapping("/todas")
    public List<Categoria> listarTodas() {
        return categoriaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaService.crear(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @RequestBody Categoria datos) {
        return categoriaService.actualizar(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Categoria> activar(@PathVariable Long id) {
        return categoriaService.cambiarEstado(id, true)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Categoria> desactivar(@PathVariable Long id) {
        return categoriaService.cambiarEstado(id, false)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/subcategorias")
    public List<Subcategoria> listarSubcategorias(@PathVariable Long id) {
        return categoriaService.listarSubcategorias(id);
    }

    @PostMapping("/subcategorias")
    public Subcategoria crearSubcategoria(@RequestBody Subcategoria sub) {
        return categoriaService.crearSubcategoria(sub);
    }
}
