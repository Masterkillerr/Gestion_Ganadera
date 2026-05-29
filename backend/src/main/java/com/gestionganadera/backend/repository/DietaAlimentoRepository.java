package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.DietaAlimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietaAlimentoRepository extends JpaRepository<DietaAlimento, Integer> {
}
