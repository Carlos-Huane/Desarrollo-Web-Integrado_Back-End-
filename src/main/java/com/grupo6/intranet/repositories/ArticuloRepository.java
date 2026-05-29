package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticuloRepository extends JpaRepository<Articulo, Long> {
    List<Articulo> findByActivoTrueOrderByCreatedAtDesc();
    List<Articulo> findByCategoriaIdAndActivoTrueOrderByCreatedAtDesc(Long categoriaId);

    @Query("SELECT a FROM Articulo a WHERE a.activo = true AND " +
           "(LOWER(a.titulo) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
           "LOWER(a.contenido) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
           "ORDER BY a.vistas DESC")
    List<Articulo> buscarPorTexto(@Param("texto") String texto);

    List<Articulo> findTop10ByActivoTrueOrderByVistasDesc();
}
