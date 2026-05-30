package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;




@Entity
@Table(name = "lote")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_finca", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "lotes"})
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
