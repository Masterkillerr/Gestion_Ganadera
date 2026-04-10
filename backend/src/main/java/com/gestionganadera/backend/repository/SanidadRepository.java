package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Sanidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SanidadRepository extends JpaRepository<Sanidad, String> {
    List<Sanidad> findByGanadoId(String ganadoId);

    List<Sanidad> findByFechaRetiroAfter(LocalDate date);
}
