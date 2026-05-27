package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Integer id;
    private String fecha;
    private String animalNombre;
    private String animalArete;
    private String origen;
    private String destino;
    private String tipoMovimiento;
}
