package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Especie;
import com.gestionganadera.backend.repository.EspecieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EspecieService {
    private final EspecieRepository especieRepository;

    public List<Especie> findAll() {
        return especieRepository.findAll();
    }
}
