package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateLoteRequest {
    @NotBlank
    private String nombre;
    @NotNull
    private Integer fincaId;
    private BigDecimal hectareas;
    private Integer capacidadMaxima;
    private String tipoPasto;
    private String estado;
}
