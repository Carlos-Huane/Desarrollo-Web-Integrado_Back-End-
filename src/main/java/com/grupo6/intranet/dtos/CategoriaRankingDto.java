package com.grupo6.intranet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoriaRankingDto {
    private Long categoriaId;
    private String nombre;
    private Long totalTicketsResueltos;
    private Double tiempoPromedioHoras;
}
