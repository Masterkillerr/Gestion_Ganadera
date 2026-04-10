package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, String> {
    List<MovimientoInventario> findByGanadoId(String ganadoId);

    List<MovimientoInventario> findByFechaBetween(LocalDate start, LocalDate end);
}
