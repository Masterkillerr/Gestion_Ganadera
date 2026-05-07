package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Parto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartoRepository extends JpaRepository<Parto, Integer> {
    List<Parto> findByReproduccionId(Integer reproduccionId);
}
