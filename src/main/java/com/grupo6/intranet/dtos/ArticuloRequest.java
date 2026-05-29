package com.grupo6.intranet.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticuloRequest {

    @NotEmpty(message = "El título es obligatorio")
    private String titulo;

    @NotEmpty(message = "El contenido es obligatorio")
    private String contenido;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
}
