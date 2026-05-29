package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReproduccionRequest {
    private Integer eventoId;
    private Integer vacaId;
    private Integer toroId;
    private Integer tipoReproduccionId;
    private Integer resultadoReproduccionId;
    private LocalDate fechaPartoEstimada;
    private String observacion;
}
