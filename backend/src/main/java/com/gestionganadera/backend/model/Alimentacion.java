package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "alimentaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alimentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id")
    private Alimento alimento;

    @Column(precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(nullable = false)
    private LocalDate fecha;
}
