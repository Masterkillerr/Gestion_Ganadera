package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "vacunaciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"animal_id", "vacuna_id", "fecha"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vacunacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacuna_id")
    private Vacuna vacuna;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "proxima_dosis")
    private LocalDate proximaDosis;
}
