package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Lote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoteDTO {
    private Integer id;
    private String nombre;
    private BigDecimal hectareas;
    private Integer capacidadMaxima;
    private String tipoPasto;
    private String estado;
    private Integer fincaId;
    private String fincaNombre;

    public static LoteDTO fromEntity(Lote lote) {
        LoteDTO dto = new LoteDTO();
        dto.setId(lote.getId());
        dto.setNombre(lote.getNombre());
        dto.setHectareas(lote.getHectareas());
        dto.setCapacidadMaxima(lote.getCapacidadMaxima());
        dto.setTipoPasto(lote.getTipoPasto());
        dto.setEstado(lote.getEstado());
        if (lote.getFinca() != null) {
            dto.setFincaId(lote.getFinca().getId());
            dto.setFincaNombre(lote.getFinca().getNombre());
        }
        return dto;
    }
}
