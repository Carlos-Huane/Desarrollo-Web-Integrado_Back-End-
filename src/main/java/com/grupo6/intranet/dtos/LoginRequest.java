package com.grupo6.intranet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "El email debe tener un @")
    @NotEmpty(message = "El email es obligatorio")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    private String password;
}
