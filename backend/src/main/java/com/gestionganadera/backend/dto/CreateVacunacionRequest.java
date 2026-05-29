package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateVacunacionRequest {
    @NotNull
    private Integer eventoId;

    @NotNull
    private Integer vacunaId;

    private LocalDate proximaDosis;

    private String observacion;
}
