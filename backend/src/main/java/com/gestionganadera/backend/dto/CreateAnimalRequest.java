package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnimalRequest {
    @Size(max = 50)
    private String identificadorArete;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "El sexo es obligatorio")
    @NotBlank(message = "El sexo es obligatorio")
    @Size(max = 20)
    private String sexo;

    @NotNull(message = "La raza es obligatoria")
    private Integer razaId;

    private Integer categoriaId;

    private Integer loteId;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @Positive(message = "El peso debe ser positivo")
    private BigDecimal pesoActual;

    @Size(max = 50)
    private String estado;

    private String fotoUrl;

    private Integer madreId;

    private Integer padreId;

    @NotNull(message = "La finca es obligatoria")
    private Integer fincaId;
}
