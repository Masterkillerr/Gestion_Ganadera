package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReproduccionRequest {
    @NotNull
    private Integer eventoId;
    @NotNull
    private Integer vacaId;
    @NotNull
    private Integer toroId;
    private Integer tipoReproduccionId;
    private Integer resultadoReproduccionId;
    private LocalDate fechaPartoEstimada;
    private String observacion;
}
