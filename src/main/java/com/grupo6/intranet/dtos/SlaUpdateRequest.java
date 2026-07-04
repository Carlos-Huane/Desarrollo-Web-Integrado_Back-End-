package com.grupo6.intranet.dtos;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlaUpdateRequest {

    @NotNull(message = "El tiempo de respuesta es obligatorio")
    @Min(value = 1, message = "El tiempo de respuesta debe ser al menos 1 hora")
    private Integer tiempoRespuestaHoras;

    @NotNull(message = "El tiempo de resolución es obligatorio")
    @Min(value = 1, message = "El tiempo de resolución debe ser al menos 1 hora")
    private Integer tiempoResolucionHoras;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @AssertTrue(message = "La respuesta debe ser menor o igual que la resolución")
    public boolean isRespuestaMenorOIgualResolucion() {
        return tiempoRespuestaHoras == null || tiempoResolucionHoras == null ||
                tiempoRespuestaHoras <= tiempoResolucionHoras;
    }
}
