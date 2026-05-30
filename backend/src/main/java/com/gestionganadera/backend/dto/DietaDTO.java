package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Dieta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaDTO {
    private Integer id;
    private String nombre;
    private String descripcion;

    public static DietaDTO fromEntity(Dieta entity) {
        DietaDTO dto = new DietaDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        return dto;
    }
}
