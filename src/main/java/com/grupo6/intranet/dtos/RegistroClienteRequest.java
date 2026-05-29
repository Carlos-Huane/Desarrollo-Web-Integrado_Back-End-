package com.grupo6.intranet.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroClienteRequest {

    @NotEmpty(message = "El nombre es obligatorio")
    private String nombre;

    @NotEmpty(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email debe tener un @")
    @NotEmpty(message = "El email es obligatorio")
    private String email;

    @NotEmpty(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener 6 caracteres mínimo")
    private String password;

    @NotEmpty(message = "Debe confirmar la contraseña")
    private String confirmPassword;

    private String telefono;
}
