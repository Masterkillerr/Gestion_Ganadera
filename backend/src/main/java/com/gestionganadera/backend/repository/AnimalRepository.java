package com.gestionganadera.backend.repository;

import com.gestionganadera.backend.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {
    Optional<Animal> findByIdentificadorArete(String identificadorArete);
    long countByEstadoAnimal_Nombre(String nombre);

    long countByEstadoAnimal_NombreIgnoreCase(String nombre);

    @Query(value = """
        SELECT categoria, COUNT(*) AS total
        FROM (
            SELECT
                CASE
                    WHEN a.fecha_nacimiento >= CURRENT_DATE - INTERVAL '12 months'
                        THEN 'terneros'
                    WHEN a.fecha_nacimiento >= CURRENT_DATE - INTERVAL '24 months'
                        THEN 'novillos'
                    ELSE 'adultos'
                END AS categoria
            FROM animal a
            WHERE a.fecha_nacimiento IS NOT NULL
        ) AS sub
        GROUP BY categoria
        ORDER BY categoria
    """, nativeQuery = true)
    List<Object[]> findDistribucionEdadRaw();
}
