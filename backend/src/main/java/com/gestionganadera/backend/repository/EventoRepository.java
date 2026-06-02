package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Evento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Integer> {
    List<Evento> findByAnimalId(Integer animalId);
    Page<Evento> findByUsuarioId(Integer usuarioId, Pageable pageable);
    Page<Evento> findByUsuarioIdOrderByFechaDesc(Integer usuarioId, Pageable pageable);
}
