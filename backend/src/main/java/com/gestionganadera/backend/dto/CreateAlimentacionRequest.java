package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlimentacionRequest {
    @NotNull
    private Integer animalId;

    @NotNull
    private Integer alimentoId;

    private BigDecimal cantidad;

    @NotNull
    private LocalDate fecha;

    private String observaciones;
}
