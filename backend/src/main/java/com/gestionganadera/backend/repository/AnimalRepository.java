package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    Optional<Animal> findByIdentificadorArete(String identificadorArete);
    List<Animal> findByFincaIdIn(List<Integer> fincaIds);
    Optional<Animal> findByIdAndFincaIdIn(Integer id, List<Integer> fincaIds);
    List<Animal> findByLoteIdAndFincaIdIn(Integer loteId, List<Integer> fincaIds);
    List<Animal> findByFincaId(Integer fincaId);
}
