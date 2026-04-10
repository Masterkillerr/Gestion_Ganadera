package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
