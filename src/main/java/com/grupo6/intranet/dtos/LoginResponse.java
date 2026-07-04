package com.grupo6.intranet.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long id;
    private String token;
    private String email;
    private String rol;
    private String nombreCompleto;
}
