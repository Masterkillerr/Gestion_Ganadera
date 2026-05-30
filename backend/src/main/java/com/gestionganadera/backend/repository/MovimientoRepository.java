package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    List<Movimiento> findByEventoId(Integer eventoId);

    @Query("SELECT m FROM Movimiento m WHERE m.loteDestino.id = :loteId " +
           "AND m.id IN (SELECT MAX(m2.id) FROM Movimiento m2 GROUP BY m2.evento.animal.id)")
    List<Movimiento> findLatestByLoteDestino(@Param("loteId") Integer loteId);

    @Query("SELECT m FROM Movimiento m WHERE m.evento.animal.id = :animalId " +
           "ORDER BY m.id DESC LIMIT 1")
    Optional<Movimiento> findUltimoByAnimalId(@Param("animalId") Integer animalId);

    @Query(value = "SELECT l.nombre FROM evento e " +
           "JOIN movimiento m ON e.id = m.id_evento " +
           "JOIN lote l ON m.id_lote_destino = l.id " +
           "JOIN animal a ON e.id_animal = a.id " +
           "WHERE a.id = :animalId " +
           "ORDER BY e.fecha DESC LIMIT 1", nativeQuery = true)
    String findUltimoLoteNombreByAnimalId(@Param("animalId") Integer animalId);

    @Query(value = "SELECT COUNT(a.id) FROM movimiento m " +
           "JOIN evento e ON e.id = m.id_evento " +
           "LEFT JOIN animal a ON a.id = e.id_animal " +
           "WHERE m.id_lote_destino = :loteId " +
           "AND m.id = (SELECT MAX(m2.id) FROM movimiento m2 WHERE m2.id_evento = m.id_evento)", nativeQuery = true)
    Integer countAnimalesByLote(@Param("loteId") Integer loteId);
}
