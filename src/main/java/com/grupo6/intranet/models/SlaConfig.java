package com.grupo6.intranet.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sla_config")
public class SlaConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Prioridad prioridad;

    @Column(name = "tiempo_respuesta_horas", nullable = false)
    private Integer tiempoRespuestaHoras;

    @Column(name = "tiempo_resolucion_horas", nullable = false)
    private Integer tiempoResolucionHoras;

    private String descripcion;
}
