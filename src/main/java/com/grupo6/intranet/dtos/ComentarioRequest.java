package com.grupo6.intranet.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ComentarioRequest {

    @NotEmpty(message = "El mensaje es obligatorio")
    private String mensaje;

    private Boolean interno = false;
}
