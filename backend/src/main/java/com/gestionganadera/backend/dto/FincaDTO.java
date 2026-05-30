package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Finca;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FincaDTO {
    private Integer id;
    private String nombre;
    private String ubicacion;
    private BigDecimal extension;

    public static FincaDTO fromEntity(Finca finca) {
        FincaDTO dto = new FincaDTO();
        dto.setId(finca.getId());
        dto.setNombre(finca.getNombre());
        dto.setUbicacion(finca.getUbicacion());
        dto.setExtension(finca.getExtension());
        return dto;
    }
}
