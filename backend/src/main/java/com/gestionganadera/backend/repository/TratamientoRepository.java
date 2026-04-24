package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {
    List<Tratamiento> findByAnimalId(Integer animalId);
}
