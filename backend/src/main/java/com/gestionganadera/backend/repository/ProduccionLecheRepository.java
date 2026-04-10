package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.ProduccionLeche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProduccionLecheRepository extends JpaRepository<ProduccionLeche, String> {
    List<ProduccionLeche> findByGanadoId(String ganadoId);

    List<ProduccionLeche> findByFechaBetween(LocalDate start, LocalDate end);
}
