package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByAnimalId(Integer animalId);

    List<Evento> findTop10ByAnimalFincaIdInOrderByFechaDesc(List<Integer> fincaIds);
}
