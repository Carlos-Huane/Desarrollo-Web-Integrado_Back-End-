package com.grupo6.intranet.services;

import com.grupo6.intranet.models.Prioridad;
import com.grupo6.intranet.models.SlaConfig;
import com.grupo6.intranet.repositories.SlaConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SlaConfigService {

    @Autowired
    private SlaConfigRepository slaConfigRepository;

    public List<SlaConfig> listarTodos() {
        return slaConfigRepository.findAll();
    }

    public Optional<SlaConfig> buscarPorPrioridad(Prioridad prioridad) {
        return slaConfigRepository.findByPrioridad(prioridad);
    }

    public Optional<SlaConfig> actualizar(Long id, SlaConfig datos) {
        return slaConfigRepository.findById(id).map(sla -> {
            sla.setTiempoRespuestaHoras(datos.getTiempoRespuestaHoras());
            sla.setTiempoResolucionHoras(datos.getTiempoResolucionHoras());
            sla.setDescripcion(datos.getDescripcion());
            return slaConfigRepository.save(sla);
        });
    }
}
