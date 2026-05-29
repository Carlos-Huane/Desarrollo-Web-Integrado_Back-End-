package com.grupo6.intranet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TecnicoConteoDto {
    private Long tecnicoId;
    private String nombreCompleto;
    private Long total;
}
