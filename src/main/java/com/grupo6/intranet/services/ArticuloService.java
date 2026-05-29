package com.grupo6.intranet.services;

import com.grupo6.intranet.dtos.ArticuloRequest;
import com.grupo6.intranet.models.Articulo;
import com.grupo6.intranet.models.Categoria;
import com.grupo6.intranet.models.Usuario;
import com.grupo6.intranet.repositories.ArticuloRepository;
import com.grupo6.intranet.repositories.CategoriaRepository;
import com.grupo6.intranet.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Articulo> listarActivos() {
        return articuloRepository.findByActivoTrueOrderByCreatedAtDesc();
    }

    public List<Articulo> listarTodos() {
        return articuloRepository.findAll();
    }

    public List<Articulo> listarPorCategoria(Long categoriaId) {
        return articuloRepository.findByCategoriaIdAndActivoTrueOrderByCreatedAtDesc(categoriaId);
    }

    public List<Articulo> buscar(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarActivos();
        }
        return articuloRepository.buscarPorTexto(texto.trim());
    }

    public List<Articulo> topVistos() {
        return articuloRepository.findTop10ByActivoTrueOrderByVistasDesc();
    }

    public Optional<Articulo> buscarPorId(Long id) {
        return articuloRepository.findById(id).map(a -> {
            a.setVistas(a.getVistas() + 1);
            return articuloRepository.save(a);
        });
    }

    public Articulo crear(ArticuloRequest req, Long autorId) {
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        Categoria categoria = categoriaRepository.findById(req.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Articulo articulo = new Articulo();
        articulo.setTitulo(req.getTitulo());
        articulo.setContenido(req.getContenido());
        articulo.setCategoria(categoria);
        articulo.setAutor(autor);
        return articuloRepository.save(articulo);
    }

    public Optional<Articulo> actualizar(Long id, ArticuloRequest req) {
        return articuloRepository.findById(id).map(articulo -> {
            articulo.setTitulo(req.getTitulo());
            articulo.setContenido(req.getContenido());
            categoriaRepository.findById(req.getCategoriaId())
                    .ifPresent(articulo::setCategoria);
            return articuloRepository.save(articulo);
        });
    }

    public Optional<Articulo> activar(Long id) {
        return articuloRepository.findById(id).map(a -> {
            a.setActivo(true);
            return articuloRepository.save(a);
        });
    }

    public Optional<Articulo> desactivar(Long id) {
        return articuloRepository.findById(id).map(a -> {
            a.setActivo(false);
            return articuloRepository.save(a);
        });
    }
}
