package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlimentacionRequest {
    @NotNull
    private Integer animalId;

    private Integer dietaId;

    @NotNull
    private LocalDateTime fecha;

    private String observacion;
}
