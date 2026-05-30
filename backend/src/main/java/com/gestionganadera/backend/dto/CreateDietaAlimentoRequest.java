package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDietaAlimentoRequest {
    @NotNull
    private Integer dietaId;

    @NotNull
    private Integer alimentoId;

    private BigDecimal cantidad;

    private String unidad;
}
