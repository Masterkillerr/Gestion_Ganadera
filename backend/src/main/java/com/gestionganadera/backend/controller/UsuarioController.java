package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateUsuarioRequest;
import com.gestionganadera.backend.dto.UpdateProfileRequest;
import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema (solo ADMIN)")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Listar usuarios", description = "Solo accesible con rol ADMIN")
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Obtener usuario por ID", description = "Solo accesible con rol ADMIN")
    public ResponseEntity<UsuarioDTO> getUsuarioById(@PathVariable @NonNull Integer id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear usuario", description = "Solo accesible con rol ADMIN")
    public ResponseEntity<UsuarioDTO> createUsuario(@Valid @RequestBody @NonNull CreateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Actualizar usuario", description = "Solo accesible con rol ADMIN")
    public ResponseEntity<UsuarioDTO> updateUsuario(@PathVariable @NonNull Integer id, @Valid @RequestBody @NonNull CreateUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Eliminar usuario", description = "Solo accesible con rol ADMIN")
    public ResponseEntity<Void> deleteUsuario(@PathVariable @NonNull Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    @Operation(summary = "Perfil propio", description = "Obtiene el perfil del usuario autenticado")
    public ResponseEntity<UsuarioDTO> getProfile() {
        return ResponseEntity.ok(usuarioService.getProfile());
    }

    @PutMapping("/profile")
    @Operation(summary = "Actualizar perfil propio")
    public ResponseEntity<UsuarioDTO> updateProfile(@Valid @RequestBody @NonNull UpdateProfileRequest request) {
        return ResponseEntity.ok(usuarioService.updateProfile(request));
    }

    @DeleteMapping(value = "/profile", produces = "application/json")
    @Operation(summary = "Eliminar cuenta propia", description = "Elimina la cuenta del usuario autenticado")
    public ResponseEntity<Void> deleteOwnAccount() {
        usuarioService.deleteOwnAccount();
        return ResponseEntity.noContent().build();
    }
}
