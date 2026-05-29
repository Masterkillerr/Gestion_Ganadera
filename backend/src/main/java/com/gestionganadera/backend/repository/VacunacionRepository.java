package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Vacunacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacunacionRepository extends JpaRepository<Vacunacion, Integer> {
    List<Vacunacion> findByEventoId(Integer eventoId);

    @Query("SELECT v FROM Vacunacion v WHERE v.evento.animal.id = :animalId")
    List<Vacunacion> findByEventoAnimalId(@Param("animalId") Integer animalId);
}
