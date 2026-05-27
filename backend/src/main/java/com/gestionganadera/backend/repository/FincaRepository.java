package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FincaRepository extends JpaRepository<Finca, Integer> {
    List<Finca> findByPropietario(Usuario propietario);
    Optional<Finca> findByIdAndPropietario(Integer id, Usuario propietario);
}
