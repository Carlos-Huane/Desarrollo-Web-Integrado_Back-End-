package com.grupo6.intranet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TecnicoRankingDto {
    private Long tecnicoId;
    private String nombreCompleto;
    private Long ticketsResueltos;
    private Double tiempoPromedioHoras;
}
