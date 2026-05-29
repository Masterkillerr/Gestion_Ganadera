package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.EstadoAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoAnimalRepository extends JpaRepository<EstadoAnimal, Integer> {
}
