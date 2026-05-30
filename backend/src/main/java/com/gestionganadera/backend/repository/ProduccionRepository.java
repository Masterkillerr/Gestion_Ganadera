package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.model.Produccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduccionRepository extends JpaRepository<Produccion, Integer> {
    List<Produccion> findByAnimalId(Integer animalId);
    List<Produccion> findByAnimalIdIn(List<Integer> animalIds);
    List<Produccion> findByAnimalIdInOrderByFechaDesc(List<Integer> animalIds);

    @Query("SELECT new com.gestionganadera.backend.dto.ProduccionResumenDTO(:year, MONTH(p.fecha), SUM(p.litros), COUNT(p)) " +
           "FROM Produccion p " +
           "WHERE YEAR(p.fecha) = :year " +
           "GROUP BY MONTH(p.fecha)")
    List<ProduccionResumenDTO> getResumenByYear(@Param("year") int year);
    @Query(value = "SELECT ROUND(AVG(total_diario), 2) FROM (SELECT SUM(litros) AS total_diario FROM produccion GROUP BY fecha) AS produccion_diaria", nativeQuery = true)
    java.math.BigDecimal getPromedioProduccionDiaria();
}
