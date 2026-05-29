package com.grupo6.intranet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResumenDashboardDto {
    private Long totalTickets;
    private Long ticketsNuevos;
    private Long ticketsEnAtencion;
    private Long ticketsEscalados;
    private Long ticketsResueltos;
    private Long ticketsCerrados;
    private Long totalUsuarios;
    private Long totalTecnicos;
    private Long totalClientes;
    private Double tiempoPromedioResolucionHoras;
}
