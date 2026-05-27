package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnimalRequest {
    private String identificadorArete;
    @NotBlank
    private String nombre;
    private String sexo;
    private Integer razaId;
    private Integer categoriaId;
    private Integer loteId;
    private LocalDate fechaNacimiento;
    private BigDecimal pesoActual;
    private String estado;
    private String fotoUrl;
    private Integer madreId;
    private Integer padreId;
    private Integer fincaId;
}
