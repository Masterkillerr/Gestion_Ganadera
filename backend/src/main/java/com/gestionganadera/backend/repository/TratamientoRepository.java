package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {
    List<Tratamiento> findByEventoId(Integer eventoId);

    @Query("SELECT t FROM Tratamiento t WHERE t.evento.animal.id = :animalId")
    List<Tratamiento> findByEventoAnimalId(@Param("animalId") Integer animalId);

    List<Tratamiento> findByUsuarioId(Integer usuarioId);
}
