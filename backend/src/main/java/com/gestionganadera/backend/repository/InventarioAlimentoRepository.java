package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.InventarioAlimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventarioAlimentoRepository extends JpaRepository<InventarioAlimento, Integer> {
}
