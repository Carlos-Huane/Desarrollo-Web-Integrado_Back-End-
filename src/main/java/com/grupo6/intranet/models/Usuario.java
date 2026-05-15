package com.grupo6.intranet.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotEmpty(message = "El apellido es obligatorio")
    @Column(nullable = false)
    private String apellido;

    @Email(message = "El email debe tener un @")
    @NotEmpty(message = "El email es obligatorio")
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @NotEmpty(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
