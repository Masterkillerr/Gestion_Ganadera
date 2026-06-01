package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    Optional<Animal> findByIdentificadorArete(String identificadorArete);
    long countByEstadoAnimal_Nombre(String nombre);

    @Query("SELECT a.fechaNacimiento FROM Animal a WHERE a.fechaNacimiento IS NOT NULL")
    List<LocalDate> findAllFechasNacimiento();

    long countByEstadoAnimal_NombreContainingIgnoreCase(String keyword);
}
