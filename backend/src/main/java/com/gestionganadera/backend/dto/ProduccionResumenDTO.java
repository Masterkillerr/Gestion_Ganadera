package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProduccionResumenDTO {
    private int year;
    private int month;
    private BigDecimal totalLitros;
    private long cantidad;
}
