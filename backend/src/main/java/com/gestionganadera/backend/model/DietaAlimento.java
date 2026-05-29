package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "dieta_alimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaAlimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dieta", nullable = false)
    private Dieta dieta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_alimento", nullable = false)
    private Alimento alimento;

    @Column(precision = 10, scale = 2)
    private BigDecimal cantidad;

    @Column(length = 50)
    private String unidad;
}
