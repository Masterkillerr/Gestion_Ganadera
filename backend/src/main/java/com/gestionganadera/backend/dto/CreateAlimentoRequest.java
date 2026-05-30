package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlimentoRequest {
    @NotBlank(message = "El nombre del alimento es obligatorio")
    @Size(max = 100)
    private String nombre;
}
