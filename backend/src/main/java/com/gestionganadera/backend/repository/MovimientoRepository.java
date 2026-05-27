package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {
    List<Movimiento> findTop10ByAnimalFincaIdInOrderByFechaDesc(List<Integer> fincaIds);
    List<Movimiento> findByAnimalFincaIdInOrderByFechaDesc(List<Integer> fincaIds);
}
