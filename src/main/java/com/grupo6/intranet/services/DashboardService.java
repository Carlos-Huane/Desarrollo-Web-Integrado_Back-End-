package com.grupo6.intranet.services;

import com.grupo6.intranet.dtos.ConteoDto;
import com.grupo6.intranet.dtos.ResumenDashboardDto;
import com.grupo6.intranet.dtos.TecnicoConteoDto;
import com.grupo6.intranet.models.Estado;
import com.grupo6.intranet.models.Prioridad;
import com.grupo6.intranet.models.Rol;
import com.grupo6.intranet.repositories.TicketRepository;
import com.grupo6.intranet.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public ResumenDashboardDto obtenerResumen() {
        Double promedio = ticketRepository.tiempoPromedioResolucionHoras();
        return new ResumenDashboardDto(
                ticketRepository.count(),
                ticketRepository.countByEstado(Estado.NUEVO),
                ticketRepository.countByEstado(Estado.EN_ATENCION),
                ticketRepository.countByEstado(Estado.ESCALADO),
                ticketRepository.countByEstado(Estado.RESUELTO),
                ticketRepository.countByEstado(Estado.CERRADO),
                usuarioRepository.count(),
                usuarioRepository.countByRol(Rol.TECNICO),
                usuarioRepository.countByRol(Rol.CLIENTE),
                promedio != null ? promedio : 0.0
        );
    }

    public List<ConteoDto> ticketsPorEstado() {
        return ticketRepository.contarPorEstado().stream()
                .map(row -> new ConteoDto(((Estado) row[0]).name(), (Long) row[1]))
                .collect(Collectors.toList());
    }

    public List<ConteoDto> ticketsPorPrioridad() {
        return ticketRepository.contarPorPrioridad().stream()
                .map(row -> new ConteoDto(((Prioridad) row[0]).name(), (Long) row[1]))
                .collect(Collectors.toList());
    }

    public List<ConteoDto> ticketsPorCategoria() {
        return ticketRepository.contarPorCategoria().stream()
                .map(row -> new ConteoDto((String) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }

    public List<TecnicoConteoDto> ticketsPorTecnico() {
        return ticketRepository.contarPorTecnico().stream()
                .map(row -> new TecnicoConteoDto(
                        (Long) row[0],
                        row[1] + " " + row[2],
                        (Long) row[3]))
                .collect(Collectors.toList());
    }
}
