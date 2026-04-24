package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {
    List<Alerta> findByLeidaFalse();
}
