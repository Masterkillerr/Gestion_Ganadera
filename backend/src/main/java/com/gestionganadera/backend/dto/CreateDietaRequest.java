package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDietaRequest {
    @NotBlank(message = "El nombre de la dieta es obligatorio")
    @Size(max = 100)
    private String nombre;

    private String descripcion;
}
