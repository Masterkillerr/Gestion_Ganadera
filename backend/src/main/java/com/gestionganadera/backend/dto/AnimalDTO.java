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

    private String razaNombre;
    private String categoriaNombre;
    private String estado;

    private BigDecimal pesoActual;
    private String loteNombre;
    private String fotoUrl;
    private Integer madreId;
    private Integer padreId;
    private String fincaNombre;
    private Integer loteId;
    private String fechaNacimiento;

    public static AnimalDTO fromEntity(Animal animal) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setIdentificadorArete(animal.getIdentificadorArete());
        dto.setNombre(animal.getNombre());
        dto.setSexo(animal.getSexo());

        dto.setRazaNombre(animal.getRaza() != null ? animal.getRaza().getNombre() : null);
        dto.setCategoriaNombre(animal.getCategoria() != null ? animal.getCategoria().getNombre() : null);
        dto.setEstado(animal.getEstado());

        dto.setPesoActual(animal.getPesoActual());
        dto.setLoteNombre(animal.getLote() != null ? animal.getLote().getNombre() : null);
        dto.setLoteId(animal.getLote() != null ? animal.getLote().getId() : null);
        dto.setFotoUrl(animal.getFotoUrl());
        dto.setMadreId(animal.getMadre() != null ? animal.getMadre().getId() : null);
        dto.setPadreId(animal.getPadre() != null ? animal.getPadre().getId() : null);
        dto.setFincaNombre(animal.getFinca() != null ? animal.getFinca().getNombre() : null);
        dto.setFechaNacimiento(animal.getFechaNacimiento() != null ? animal.getFechaNacimiento().toString() : null);
        return dto;
    }
}
