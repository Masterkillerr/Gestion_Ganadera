package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Ganado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GanadoRepository extends JpaRepository<Ganado, String> {
    Optional<Ganado> findByIdentificador(String identificador);

    List<Ganado> findByLoteId(String loteId);

    List<Ganado> findByEstado(Ganado.Estado estado);
}
