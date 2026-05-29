package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Finca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FincaRepository extends JpaRepository<Finca, Integer> {
}
