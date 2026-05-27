package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovimientoRequest {
    @NotNull
    private Integer animalId;

    private Integer loteOrigenId;

    @NotNull
    private Integer loteDestinoId;

    @NotNull
    private LocalDate fecha;

    private String tipoMovimiento;

    private String motivo;
}
