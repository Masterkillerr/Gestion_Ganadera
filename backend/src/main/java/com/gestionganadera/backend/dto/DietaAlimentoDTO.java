package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.DietaAlimento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaAlimentoDTO {
    private Integer id;
    private Integer dietaId;
    private String dietaNombre;
    private Integer alimentoId;
    private String alimentoNombre;
    private BigDecimal cantidad;
    private String unidad;

    public static DietaAlimentoDTO fromEntity(DietaAlimento entity) {
        DietaAlimentoDTO dto = new DietaAlimentoDTO();
        dto.setId(entity.getId());
        if (entity.getDieta() != null) {
            dto.setDietaId(entity.getDieta().getId());
            dto.setDietaNombre(entity.getDieta().getNombre());
        }
        if (entity.getAlimento() != null) {
            dto.setAlimentoId(entity.getAlimento().getId());
            dto.setAlimentoNombre(entity.getAlimento().getNombre());
        }
        dto.setCantidad(entity.getCantidad());
        dto.setUnidad(entity.getUnidad());
        return dto;
    }
}
