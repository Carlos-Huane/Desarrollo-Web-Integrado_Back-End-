package com.grupo6.intranet.repositories;

import com.grupo6.intranet.models.Prioridad;
import com.grupo6.intranet.models.SlaConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SlaConfigRepository extends JpaRepository<SlaConfig, Long> {
    Optional<SlaConfig> findByPrioridad(Prioridad prioridad);
}
