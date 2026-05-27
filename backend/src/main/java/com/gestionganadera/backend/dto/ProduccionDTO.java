package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduccionDTO {
    private Integer id;
    private Integer animalId;
    private String animalNombre;
    private String animalArete;
    private BigDecimal litros;
    private String turno;
    private LocalDate fecha;
}
