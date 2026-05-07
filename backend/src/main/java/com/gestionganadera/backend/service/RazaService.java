package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Raza;
import com.gestionganadera.backend.repository.RazaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RazaService {
    private final RazaRepository razaRepository;

    public List<Raza> findAll() {
        return razaRepository.findAll();
    }
}
