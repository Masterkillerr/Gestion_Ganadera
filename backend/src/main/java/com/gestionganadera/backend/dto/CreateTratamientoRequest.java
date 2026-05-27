package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTratamientoRequest {
    @NotNull
    private Integer animalId;

    @NotNull
    private Integer medicamentoId;

    private String dosis;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Integer diasRetiro;

    private String observaciones;
}
