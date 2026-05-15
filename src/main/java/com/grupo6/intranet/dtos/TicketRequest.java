package com.grupo6.intranet.dtos;

import com.grupo6.intranet.models.Prioridad;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TicketRequest {

    @NotEmpty(message = "El título es obligatorio")
    private String titulo;

    @NotEmpty(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;

    private Long subcategoriaId;

    private Prioridad prioridad;
}
