package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Vacunacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacunacionDTO {
    private Integer id;
    private Integer eventoId;
    private String eventoDescripcion;
    private Integer animalId;
    private String animalNombre;
    private String animalArete;
    private Integer vacunaId;
    private String vacunaNombre;
    private LocalDate proximaDosis;
    private String observacion;

    public static VacunacionDTO fromEntity(Vacunacion entity) {
        VacunacionDTO dto = new VacunacionDTO();
        dto.setId(entity.getId());
        if (entity.getEvento() != null) {
            dto.setEventoId(entity.getEvento().getId());
            dto.setEventoDescripcion(entity.getEvento().getDescripcion());
            if (entity.getEvento().getAnimal() != null) {
                dto.setAnimalId(entity.getEvento().getAnimal().getId());
                dto.setAnimalNombre(entity.getEvento().getAnimal().getNombre());
                dto.setAnimalArete(entity.getEvento().getAnimal().getIdentificadorArete());
            }
        }
        if (entity.getVacuna() != null) {
            dto.setVacunaId(entity.getVacuna().getId());
            dto.setVacunaNombre(entity.getVacuna().getNombre());
        }
        dto.setProximaDosis(entity.getProximaDosis());
        dto.setObservacion(entity.getObservacion());
        return dto;
    }
}
