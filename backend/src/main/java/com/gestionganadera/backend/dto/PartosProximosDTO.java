package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartosProximosDTO {
    private Integer reproduccionId;
    private String vacaNombre;
    private String vacaArete;
    private String toroNombre;
    private String toroArete;
    private LocalDate fechaPartoEstimada;
    private long diasRestantes;
}
