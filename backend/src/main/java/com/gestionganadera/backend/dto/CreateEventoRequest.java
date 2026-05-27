package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventoRequest {
    @NotNull
    private Integer animalId;

    @NotNull
    private String tipo;

    private String descripcion;
}
