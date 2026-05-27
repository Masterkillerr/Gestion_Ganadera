package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private UUID id;
    private String nombre;
    private String email;
    private String role;
    private LocalDateTime creadoEn;

    public static UsuarioResponse fromEntity(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRole(usuario.getRole() != null ? usuario.getRole().getNombre() : "USER");
        dto.setCreadoEn(usuario.getCreadoEn());
        return dto;
    }
}
