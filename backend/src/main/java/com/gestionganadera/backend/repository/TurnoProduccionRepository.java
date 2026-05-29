package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.TurnoProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TurnoProduccionRepository extends JpaRepository<TurnoProduccion, Integer> {
}
