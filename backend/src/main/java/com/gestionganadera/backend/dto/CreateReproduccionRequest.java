package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReproduccionRequest {
    private Integer vacaId;
    private Integer toroId;
    private LocalDate fechaMonta;
    private String tipo;
    private String resultado;
    private LocalDate fechaPartoEstimada;
    private String observaciones;
}
