package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Especie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EspecieRepository extends JpaRepository<Especie, Integer> {
}
