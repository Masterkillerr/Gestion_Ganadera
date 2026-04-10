package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Ganado;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GanadoDTO {
    private String id;
    private String identificador;
    private String raza;
    private String sexo;
    private String estado;
    private Double peso;
    private String loteNombre;

    public static GanadoDTO fromEntity(Ganado ganado) {
        GanadoDTO dto = new GanadoDTO();
        dto.setId(ganado.getId());
        dto.setIdentificador(ganado.getIdentificador());
        dto.setRaza(ganado.getRaza());
        dto.setSexo(ganado.getSexo().name());
        dto.setEstado(ganado.getEstado().name());
        dto.setPeso(ganado.getPeso());
        dto.setLoteNombre(ganado.getLote().getNombre());
        return dto;
    }
}
