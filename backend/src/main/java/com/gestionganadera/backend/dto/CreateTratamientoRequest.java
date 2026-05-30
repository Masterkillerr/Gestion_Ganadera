package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTratamientoRequest {
    @NotNull
    private Integer eventoId;

    @NotNull
    private Integer medicamentoId;

    private String dosisMl;

    @NotNull
    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String observacion;
}
