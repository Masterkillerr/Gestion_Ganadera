package com.gestionganadera.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "reproducciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaca_id")
    private Animal vaca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toro_id")
    private Animal toro;

    @Column(name = "fecha_monta")
    private LocalDate fechaMonta;

    @Column(length = 50)
    private String tipo;



    @Column(length = 100)
    private String resultado;

    @Column(name = "fecha_parto_estimada")
    private LocalDate fechaPartoEstimada;

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
