package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartoRequest {
    private Integer reproduccionId;
    private LocalDate fechaParto;
    private Integer cantidadCrias;
    private String observaciones;
}
