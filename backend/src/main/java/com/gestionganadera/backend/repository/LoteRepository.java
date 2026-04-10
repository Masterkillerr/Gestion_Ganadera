package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, String> {
    List<Lote> findByFincaId(String fincaId);
}
