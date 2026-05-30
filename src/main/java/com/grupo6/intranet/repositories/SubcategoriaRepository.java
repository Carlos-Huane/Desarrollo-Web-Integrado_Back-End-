package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubcategoriaRepository extends JpaRepository<Subcategoria, Long> {
    List<Subcategoria> findByCategoriaId(Long categoriaId);
    List<Subcategoria> findByActivoTrue();
}
