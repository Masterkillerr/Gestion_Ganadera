package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Alimentacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlimentacionDTO {
    private Integer id;
    private Integer animalId;
    private String animalNombre;
    private String animalArete;
    private Integer dietaId;
    private String dietaNombre;
    private LocalDateTime fecha;
    private String observacion;

    public static AlimentacionDTO fromEntity(Alimentacion entity) {
        AlimentacionDTO dto = new AlimentacionDTO();
        dto.setId(entity.getId());
        if (entity.getAnimal() != null) {
            dto.setAnimalId(entity.getAnimal().getId());
            dto.setAnimalNombre(entity.getAnimal().getNombre());
            dto.setAnimalArete(entity.getAnimal().getIdentificadorArete());
        }
        if (entity.getDieta() != null) {
            dto.setDietaId(entity.getDieta().getId());
            dto.setDietaNombre(entity.getDieta().getNombre());
        }
        dto.setFecha(entity.getFecha());
        dto.setObservacion(entity.getObservacion());
        return dto;
    }
}
