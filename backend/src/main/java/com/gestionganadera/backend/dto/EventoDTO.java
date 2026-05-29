package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private Integer id;
    private String fecha;
    private String tipoEvento;
    private String descripcion;
    private Integer animalId;
    private String animalNombre;
    private String animalArete;
}
