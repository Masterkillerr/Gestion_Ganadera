package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    List<Movimiento> findByEventoId(Integer eventoId);

    @Query("SELECT m FROM Movimiento m WHERE m.loteDestino.id = :loteId " +
           "AND m.id IN (SELECT MAX(m2.id) FROM Movimiento m2 GROUP BY m2.evento.animal.id)")
    List<Movimiento> findLatestByLoteDestino(@Param("loteId") Integer loteId);
}
