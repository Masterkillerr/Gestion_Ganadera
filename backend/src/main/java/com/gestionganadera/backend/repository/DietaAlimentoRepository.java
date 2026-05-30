package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.DietaAlimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietaAlimentoRepository extends JpaRepository<DietaAlimento, Integer> {
    List<DietaAlimento> findByDietaId(Integer dietaId);

    void deleteByDietaId(Integer dietaId);
}
