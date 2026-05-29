package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartoRequest {
    private Integer eventoId;
    private Integer reproduccionId;
    private Integer cantidadCrias;
    private String observacion;
}
