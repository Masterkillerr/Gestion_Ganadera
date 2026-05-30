package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartoRequest {
    @NotNull
    private Integer eventoId;
    @NotNull
    private Integer reproduccionId;
    private Integer cantidadCrias;
    private String observacion;
    private LocalDate fechaParto;
}
