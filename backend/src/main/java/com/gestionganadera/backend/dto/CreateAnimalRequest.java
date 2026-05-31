package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
    @NotBlank(message = "El identificador del arete es obligatorio")
    @Size(max = 100)
    private String identificadorArete;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    private Integer sexoId;

    private Integer estadoAnimalId;

    @NotNull(message = "La raza es obligatoria")
    private Integer razaId;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento no puede ser futura")
    private LocalDate fechaNacimiento;

    @Positive(message = "El peso debe ser positivo")
    private BigDecimal pesoActualKg;

    private String fotoUrl;

    private Integer madreId;

    private Integer padreId;
}
