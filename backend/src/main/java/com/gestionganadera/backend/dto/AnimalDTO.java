package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Animal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDTO {
    private Integer id;
    private String identificadorArete;
    private String nombre;
    private String sexo;
    private String estadoAnimal;
    private String razaNombre;
    private BigDecimal pesoActualKg;
    private String fotoUrl;
    private Integer madreId;
    private String madreArete;
    private String madreNombre;
    private Integer padreId;
    private String padreArete;
    private String padreNombre;
    private String fechaNacimiento;

    public static AnimalDTO fromEntity(Animal animal) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setIdentificadorArete(animal.getIdentificadorArete());
        dto.setNombre(animal.getNombre());
        dto.setSexo(animal.getSexo() != null ? animal.getSexo().getNombre() : null);
        dto.setEstadoAnimal(animal.getEstadoAnimal() != null ? animal.getEstadoAnimal().getNombre() : null);
        dto.setRazaNombre(animal.getRaza() != null ? animal.getRaza().getNombre() : null);
        dto.setPesoActualKg(animal.getPesoActualKg());
        dto.setFotoUrl(animal.getFotoUrl());
        if (animal.getMadre() != null) {
            dto.setMadreId(animal.getMadre().getId());
            dto.setMadreArete(animal.getMadre().getIdentificadorArete());
            dto.setMadreNombre(animal.getMadre().getNombre());
        }
        if (animal.getPadre() != null) {
            dto.setPadreId(animal.getPadre().getId());
            dto.setPadreArete(animal.getPadre().getIdentificadorArete());
            dto.setPadreNombre(animal.getPadre().getNombre());
        }
        dto.setFechaNacimiento(animal.getFechaNacimiento() != null ? animal.getFechaNacimiento().toString() : null);
        return dto;
    }
}
