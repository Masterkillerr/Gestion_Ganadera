package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateUsuarioRequest;
import com.gestionganadera.backend.dto.UpdateProfileRequest;
import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.exception.DuplicateResourceException;
import com.gestionganadera.backend.exception.ResourceNotFoundException;
import com.gestionganadera.backend.exception.IllegalOperationException;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> findById(@NonNull Integer id) {
        return usuarioRepository.findById(id)
                .map(UsuarioDTO::fromEntity);
    }

    public UsuarioDTO create(@NonNull CreateUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword() != null ? request.getPassword() : "default123"));

        if (request.getRol() != null) {
            Role role = roleRepository.findByNombre(request.getRol())
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + request.getRol()));
            usuario.setRole(role);
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(usuario));
    }

    public UsuarioDTO update(@NonNull Integer id, @NonNull CreateUsuarioRequest request) {
        Usuario currentUser = getCurrentUser();
        return usuarioRepository.findById(id)
                .map(existing -> {
                    boolean isTargetAdmin = "ADMINISTRADOR".equals(existing.getRole().getNombre());
                    boolean isSelf = currentUser.getId().equals(existing.getId());

                    // No permitir cambiar el rol de otro administrador
                    if (isTargetAdmin && !isSelf) {
                        throw new IllegalOperationException("No puedes modificar a otro administrador");
                    }

                    // No permitir que un admin se cambie su propio rol
                    if (isSelf && request.getRol() != null && !"ADMINISTRADOR".equals(request.getRol())) {
                        throw new IllegalOperationException("No puedes cambiar tu propio rol de Administrador");
                    }

                    if (request.getNombre() != null) {
                        existing.setNombre(request.getNombre());
                    }
                    if (request.getEmail() != null && !request.getEmail().equals(existing.getEmail())) {
                        if (usuarioRepository.existsByEmail(request.getEmail())) {
                            throw new DuplicateResourceException("El email ya está registrado");
                        }
                        existing.setEmail(request.getEmail());
                    }
                    if (request.getPassword() != null && !request.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(request.getPassword()));
                    }
                    if (request.getRol() != null) {
                        Role role = roleRepository.findByNombre(request.getRol())
                                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + request.getRol()));
                        existing.setRole(role);
                    }
                    return UsuarioDTO.fromEntity(usuarioRepository.save(existing));
                }).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        Usuario currentUser = getCurrentUser();
        usuarioRepository.findById(id)
                .ifPresentOrElse(
                    usuario -> {
                        boolean isTargetAdmin = "ADMINISTRADOR".equals(usuario.getRole().getNombre());
                        boolean isSelf = currentUser.getId().equals(usuario.getId());

                        // No permitir eliminar a otro administrador
                        if (isTargetAdmin && !isSelf) {
                            throw new IllegalOperationException("No puedes eliminar a otro administrador");
                        }

                        // No permitir eliminarse a sí mismo si es el único admin
                        if (isSelf && isTargetAdmin) {
                            long adminCount = usuarioRepository.countByRoleNombre("ADMINISTRADOR");
                            if (adminCount <= 1) {
                                throw new IllegalOperationException("No puedes eliminar la única cuenta de administrador del sistema");
                            }
                        }

                        usuarioRepository.deleteById(id);
                    },
                    () -> { throw new ResourceNotFoundException("Usuario no encontrado"); }
                );
    }

    public UsuarioDTO getProfile() {
        return UsuarioDTO.fromEntity(getCurrentUser());
    }

    public void deleteOwnAccount() {
        Usuario currentUser = getCurrentUser();
        usuarioRepository.deleteById(currentUser.getId());
    }

    public UsuarioDTO updateProfile(@NonNull UpdateProfileRequest request) {
        Usuario currentUser = getCurrentUser();

        if (request.getNombre() != null) {
            currentUser.setNombre(request.getNombre());
        }
        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail())) {
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("El email ya está registrado");
            }
            currentUser.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return UsuarioDTO.fromEntity(usuarioRepository.save(currentUser));
    }
}
