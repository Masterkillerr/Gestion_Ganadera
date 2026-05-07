package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
}
