package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Vacunacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Integer> {
    List<Vacunacion> findByAnimalId(Integer animalId);
}
