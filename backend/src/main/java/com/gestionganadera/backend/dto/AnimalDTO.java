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
    private String identificador;
    private String especieNombre;
    private String razaNombre;
    private String categoriaNombre;
    private String estado;
    private BigDecimal peso;
    private String loteNombre;

    public static AnimalDTO fromEntity(Animal animal) {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(animal.getId());
        dto.setIdentificador(animal.getIdentificador());
        dto.setEspecieNombre(animal.getEspecie() != null ? animal.getEspecie().getNombre() : null);
        dto.setRazaNombre(animal.getRaza() != null ? animal.getRaza().getNombre() : null);
        dto.setCategoriaNombre(animal.getCategoria() != null ? animal.getCategoria().getNombre() : null);
        dto.setEstado(animal.getEstado());
        dto.setPeso(animal.getPeso());
        dto.setLoteNombre(animal.getLote() != null ? animal.getLote().getNombre() : null);
        return dto;
    }
}
