package com.gestionganadera.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovimientoRequest {
    @NotNull
    private Integer eventoId;

    private Integer tipoMovimientoId;

    private Integer loteOrigenId;

    @NotNull
    private Integer loteDestinoId;

    private String motivo;
}
