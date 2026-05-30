package com.grupo6.intranet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConteoDto {
    private String etiqueta;
    private Long total;
}
