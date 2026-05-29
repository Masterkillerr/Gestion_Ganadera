package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventoRequest {
    @NotNull
    private Integer animalId;

    @NotNull
    private Integer tipoEventoId;

    private String descripcion;

    private LocalDateTime fecha;
}
