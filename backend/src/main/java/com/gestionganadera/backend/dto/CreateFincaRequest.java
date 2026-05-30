package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFincaRequest {
    @NotBlank
    private String nombre;
    private String ubicacion;
    private BigDecimal extension;
}
