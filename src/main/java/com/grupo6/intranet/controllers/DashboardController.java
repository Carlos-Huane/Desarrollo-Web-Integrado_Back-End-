package com.grupo6.intranet.controllers;

import com.grupo6.intranet.dtos.ConteoDto;
import com.grupo6.intranet.dtos.ResumenDashboardDto;
import com.grupo6.intranet.dtos.TecnicoConteoDto;
import com.grupo6.intranet.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/resumen")
    public ResumenDashboardDto resumen() {
        return dashboardService.obtenerResumen();
    }

    @GetMapping("/tickets-por-estado")
    public List<ConteoDto> ticketsPorEstado() {
        return dashboardService.ticketsPorEstado();
    }

    @GetMapping("/tickets-por-prioridad")
    public List<ConteoDto> ticketsPorPrioridad() {
        return dashboardService.ticketsPorPrioridad();
    }

    @GetMapping("/tickets-por-categoria")
    public List<ConteoDto> ticketsPorCategoria() {
        return dashboardService.ticketsPorCategoria();
    }

    @GetMapping("/tickets-por-tecnico")
    public List<TecnicoConteoDto> ticketsPorTecnico() {
        return dashboardService.ticketsPorTecnico();
    }
}
