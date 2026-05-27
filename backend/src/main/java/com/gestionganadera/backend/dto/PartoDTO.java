package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartoDTO {
    private Integer id;
    private Integer reproduccionId;
    private String vacaNombre;
    private String vacaArete;
    private LocalDate fechaParto;
    private Integer cantidadCrias;
    private String observaciones;
}
