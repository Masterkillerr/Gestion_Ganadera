package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.TipoReproduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoReproduccionRepository extends JpaRepository<TipoReproduccion, Integer> {
}
