package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Dieta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietaRepository extends JpaRepository<Dieta, Integer> {
}
