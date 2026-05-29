package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReproduccionDTO {
    private Integer id;
    private Integer eventoId;
    private String fechaMonta;
    private Integer vacaId;
    private String vacaNombre;
    private String vacaArete;
    private Integer toroId;
    private String toroNombre;
    private String toroArete;
    private String tipoReproduccion;
    private String resultadoReproduccion;
    private LocalDate fechaPartoEstimada;
    private String observacion;
}
