package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    private Integer id;
    private Integer eventoId;
    private String animalNombre;
    private String animalArete;
    private String tipoMovimiento;
    private String origen;
    private Integer origenId;
    private String destino;
    private Integer destinoId;
    private String motivo;
    private String fecha;
}
