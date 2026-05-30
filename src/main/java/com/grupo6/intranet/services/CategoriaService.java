package com.grupo6.intranet.services;

import com.grupo6.intranet.models.Categoria;
import com.grupo6.intranet.models.Subcategoria;
import com.grupo6.intranet.repositories.CategoriaRepository;
import com.grupo6.intranet.repositories.SubcategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private SubcategoriaRepository subcategoriaRepository;

    public List<Categoria> listarActivas() {
        return categoriaRepository.findByActivoTrue();
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Transactional
    public Categoria crear(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Optional<Categoria> actualizar(Long id, Categoria datos) {
        return categoriaRepository.findById(id).map(c -> {
            c.setNombre(datos.getNombre());
            c.setDescripcion(datos.getDescripcion());
            return categoriaRepository.save(c);
        });
    }

    @Transactional
    public Optional<Categoria> cambiarEstado(Long id, boolean activo) {
        return categoriaRepository.findById(id).map(c -> {
            c.setActivo(activo);
            return categoriaRepository.save(c);
        });
    }

    public List<Subcategoria> listarSubcategorias(Long categoriaId) {
        return subcategoriaRepository.findByCategoriaId(categoriaId);
    }

    @Transactional
    public Subcategoria crearSubcategoria(Subcategoria sub) {
        return subcategoriaRepository.save(sub);
    }
}
