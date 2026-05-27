package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Produccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduccionRepository extends JpaRepository<Produccion, Integer> {
    List<Produccion> findByAnimalId(Integer animalId);
    List<Produccion> findByAnimalIdIn(List<Integer> animalIds);
    List<Produccion> findByAnimalIdInOrderByFechaDesc(List<Integer> animalIds);
}
