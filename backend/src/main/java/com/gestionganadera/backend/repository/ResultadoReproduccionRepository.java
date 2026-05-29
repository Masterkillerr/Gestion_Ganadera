package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.ResultadoReproduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultadoReproduccionRepository extends JpaRepository<ResultadoReproduccion, Integer> {
}
