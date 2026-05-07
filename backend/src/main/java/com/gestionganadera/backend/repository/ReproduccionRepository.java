package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Reproduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReproduccionRepository extends JpaRepository<Reproduccion, Integer> {
}
