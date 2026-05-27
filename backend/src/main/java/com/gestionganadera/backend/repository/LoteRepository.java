package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    List<Lote> findByFincaIdIn(List<Integer> fincaIds);
    Optional<Lote> findByIdAndFincaIdIn(Integer id, List<Integer> fincaIds);
    List<Lote> findByFincaId(Integer fincaId);
}
