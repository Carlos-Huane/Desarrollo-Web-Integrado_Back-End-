package com.grupo6.intranet.dtos;

import com.grupo6.intranet.models.Estado;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CambioEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private Estado estadoNuevo;

    private String comentario;

    private Long tecnicoId;
}
