package com.gestionganadera.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartoDTO {
    private Integer id;
    private Integer eventoId;
    private String fechaParto;
    private Integer reproduccionId;
    private String vacaNombre;
    private String vacaArete;
    private Integer cantidadCrias;
    private String observacion;
}
