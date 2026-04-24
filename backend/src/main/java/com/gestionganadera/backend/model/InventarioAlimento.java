package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "inventario_alimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioAlimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alimento_id")
    private Alimento alimento;

    @Column(precision = 10, scale = 2)
    private BigDecimal stock;

    @Column(length = 20)
    private String unidad;
}
