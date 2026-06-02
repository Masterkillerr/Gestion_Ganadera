package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT l.id FROM Movimiento m " +
           "JOIN m.evento e " +
           "JOIN m.loteDestino l " +
           "WHERE e.animal.id = :animalId " +
           "ORDER BY e.fecha DESC")
    Optional<Integer> findUltimoLoteIdByAnimalId(@Param("animalId") Integer animalId);

    @Query("SELECT COUNT(DISTINCT e.animal.id) FROM Movimiento m " +
           "JOIN m.evento e " +
           "WHERE m.loteDestino.id = :loteId")
    Integer countAnimalesByLote(@Param("loteId") Integer loteId);

    @Query("SELECT m FROM Movimiento m " +
           "LEFT JOIN m.evento e " +
           "LEFT JOIN e.animal a " +
           "LEFT JOIN m.loteOrigen lo " +
           "LEFT JOIN m.loteDestino ld " +
           "LEFT JOIN m.tipoMovimiento tm " +
           "WHERE (:search IS NULL OR :search = '' OR " +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.identificadorArete) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(lo.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ld.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(tm.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.motivo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Movimiento> findBySearch(@Param("search") String search, Pageable pageable);

    Page<Movimiento> findByUsuarioId(Integer usuarioId, Pageable pageable);

    @Query("SELECT m FROM Movimiento m WHERE m.usuario.id = :usuarioId ORDER BY m.evento.fecha DESC")
    Page<Movimiento> findByUsuarioIdOrderByFechaDesc(@Param("usuarioId") Integer usuarioId, Pageable pageable);

    @Query("SELECT m FROM Movimiento m " +
           "LEFT JOIN m.evento e " +
           "LEFT JOIN e.animal a " +
           "LEFT JOIN m.loteOrigen lo " +
           "LEFT JOIN m.loteDestino ld " +
           "LEFT JOIN m.tipoMovimiento tm " +
           "WHERE m.usuario.id = :usuarioId AND (:search IS NULL OR :search = '' OR " +
           "LOWER(a.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.identificadorArete) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(lo.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ld.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(tm.nombre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.motivo) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Movimiento> findBySearchAndUsuarioId(@Param("search") String search, @Param("usuarioId") Integer usuarioId, Pageable pageable);
}
