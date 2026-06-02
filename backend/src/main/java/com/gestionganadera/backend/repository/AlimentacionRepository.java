package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Alimentacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlimentacionRepository extends JpaRepository<Alimentacion, Integer> {
    List<Alimentacion> findByAnimalId(Integer animalId);
    Page<Alimentacion> findByUsuarioId(Integer usuarioId, Pageable pageable);
}
