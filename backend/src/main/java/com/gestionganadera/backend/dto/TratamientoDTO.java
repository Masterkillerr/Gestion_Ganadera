package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Tratamiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TratamientoDTO {
    private Integer id;
    private Integer eventoId;
    private String eventoDescripcion;
    private Integer animalId;
    private String animalNombre;
    private String animalArete;
    private Integer medicamentoId;
    private String medicamentoNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String dosisMl;
    private String observacion;

    public static TratamientoDTO fromEntity(Tratamiento entity) {
        TratamientoDTO dto = new TratamientoDTO();
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
        if (entity.getMedicamento() != null) {
            dto.setMedicamentoId(entity.getMedicamento().getId());
            dto.setMedicamentoNombre(entity.getMedicamento().getNombre());
        }
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setDosisMl(entity.getDosisMl());
        dto.setObservacion(entity.getObservacion());
        return dto;
    }
}
