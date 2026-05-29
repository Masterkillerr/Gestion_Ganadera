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
public class CreateProduccionRequest {
    @NotNull
    private Integer animalId;

    private BigDecimal litros;

    private Integer turnoProduccionId;

    @NotNull
    private LocalDate fecha;
}
