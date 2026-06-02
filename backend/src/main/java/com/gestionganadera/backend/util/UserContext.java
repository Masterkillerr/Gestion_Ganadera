package com.gestionganadera.backend.util;

import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserContext {

    private final UsuarioRepository usuarioRepository;

    public Usuario getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("No authenticated user found");
        }

        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado: " + email));
    }

    public Integer getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser");
    }
}
