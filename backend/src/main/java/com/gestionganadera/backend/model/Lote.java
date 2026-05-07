package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Entity
@Table(name = "lotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finca_id")
    private Finca finca;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal hectareas;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @Column(name = "tipo_pasto", length = 100)
    private String tipoPasto;





    @Column(length = 50)
    private String estado;
}
