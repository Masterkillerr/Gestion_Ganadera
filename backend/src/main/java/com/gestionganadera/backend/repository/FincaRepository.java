package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Finca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FincaRepository extends JpaRepository<Finca, Integer> {
    List<Finca> findByUsuarioId(Integer usuarioId);
}
